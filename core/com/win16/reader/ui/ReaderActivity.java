package com.win16.reader.ui;

import java.io.File;
import java.util.Random;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.ads.Ad;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdRequest.ErrorCode;
import com.win16.data.GlobalDataManager;
import com.win16.reader.data.ArticleDocument;
import com.win16.reader.data.Configuration;
import com.win16.reader.data.DynamicConfigure;
import com.win16.reader.data.UserProgressData;
import com.win16.reader.data.UserProgressManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.ui.widget.CustomerMenu;
import com.win16.reader.ui.widget.ReadView;
import com.win16.utils.Tools;

public class ReaderActivity extends BaseActivity
{

	/**
	 * 文档数据
	 */
	private static ArticleDocument doc;

	@Override
	protected void onRestart()
	{
		// TODO Auto-generated method stub
		super.onRestart();
	}

	private ReadView textView;

	private ScrollView scrollView;

	private final int REQUEST_MENU = 0;

	private static ReaderActivity instance;

	protected static final int GET_TEXT_DONE = 1;
	protected static final int SET_TEXTVIEW_TEXT_DONE = 2;
	private static final int LOAD_FROM_MARK = 3;
	protected static final int LOAD_FROM_PRELOADED = 4;
	protected static final int RESCROLL_VIEW = 5;

	private long scrollHightBefore;
	private long currentYBefore;

	private String novelName;

	private boolean disableJumpMenu = true;
	private boolean isFullScreen;

	private int oldScreenLight = 0;

	public final static int FONT_SETUP = 1;
	public final static int SAVE_MARK = 0;
	public final static int LOAD_MARK = 4;
	public final static int AUTO_SCROLL = 11;
	public final static int ADV_SETUP = 2;
	public final static int MORE_NOVEL = 3;
	public final static int LIGHT_SETUP = 5;
	public final static int EXIT_PROGRAME = 7;
	public final static int DO_NOTHING = -1;
	public final static int SCROLL_SPEED = 6;
	public final static int SEARCH_TITLE = 0;
	/** 菜单图片 **/
	int[] menu_image_array =
	{ R.drawable.dpjz, R.drawable.fkjz, R.drawable.fhml, R.drawable.jrqq, R.drawable.hdcp, R.drawable.hyjz, R.drawable.sjcp, R.drawable.jgcp };

	/** 菜单文字 **/
	String[] menu_name_array = new String[8];
	private CustomerMenu qqmenu;

	private boolean onChangeScreen = false;
	/**
	 * 存放文章内容
	 */
	private static String articleContent;

	private UserProgressData userDataBeforeJump;

	@Override
	protected void onPause()
	{
		// 
		super.onPause();
		pausedScroll = true;
		autoSave();

		unlockScreen();
	}

	private void autoSave()
	{
		if (Configuration.getInstance().isAutoSave() && articleContent != null && !articleContent.startsWith("错误"))
		{
			UserProgressData data = getCurrentReadProgress();
			if (data != null)
			{
				data.saveAuto(UserProgressData.AUTO_SAVE);
				data.saveAuto(UserProgressData.BOOK_SAVE);
				Configuration.getInstance().setReadingArticle(articleId);
			}

		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		// Log.i("Reader", "onResume");
		pausedScroll = false;
		if (bScreenChanged && !onChangeScreen)
		{
			// Log.i("Reader", "onResume in change");
			bScreenChanged = false;
			makeToast(0, R.string.setup_waiting, Toast.LENGTH_SHORT, this).show();
			if (Configuration.getInstance().isLand())
			{

				ReaderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else
			{

				ReaderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
		if (isAutoScroll)
		{
			lockScreen();
		}
//		if (!showingTime)
//		{
//			showTimeHandler.sendEmptyMessageDelayed(0, 1000 * 10);
//		}

	}

	public static boolean isAutoScroll = false;

	public int scrollSpeed = Constant.DEFAULT_SCROLL_SPEED;

	/**
	 * 暂停或继续自动滚动
	 */
	private boolean pausedScroll = false;

	/**
	 * 
	 */
	private boolean needReScroll = true;

	private Handler loadHandler = new Handler()
	{
		private float progress = 0;

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			UserProgressData data = null;
			long countLine = 0;
			long lineHeight = 0;
			long curpos = 0;

			// Log.i("Reader", "append");
			long newCount = 0;
			switch (msg.what)
			{
				case GET_TEXT_DONE:
					// Log.i("Reader", "GET_TEXT_DONE");
					textView.setText(getArticleContent());
					setArticleContent("");
					Message msgtp = new Message();
					msgtp.what = SET_TEXTVIEW_TEXT_DONE;
					sendMessage(msgtp);
					break;
				case SET_TEXTVIEW_TEXT_DONE:
					// try {
					// if (loadingDialog != null && loadingDialog.isShowing()) {
					// loadingDialog.dismiss();
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					break;
				case LOAD_FROM_MARK:
					// Log.i("Reader", "LOAD_FROM_MARK");
					autoSave();
					data = (UserProgressData) msg.obj;
					articleId = data.getBookId();

					String path = Tools.getBookPath(ReaderActivity.this, articleId);
					;

					novelName = data.getBookName();
					if (novelName == null || novelName.length() == 0)
					{
						novelName = Constant.BOOK_NAME[articleId];
					}
					bookName.setText(novelName);
					// Log.i("Reader","path="+path);
					// Log.i("Reader","getDoc="+getDoc());
					// Log.i("Reader","getDoc().getFilePath()="+getDoc().getFilePath());

					if (!path.equalsIgnoreCase(getDoc().getFilePath()))
					{

						File file = new File(path);
						if (!file.exists())
						{
							path = Tools.copyFile(data.getBookId());
						}
						// Log.i("Reader", "loadhandler"+path);
						getDoc().openFile(path);
						Configuration.getInstance().setReadingArticle(articleId);
						setArticleContent(getDoc().getNext());
						// Log.i("Reader", "getNext");
						textView.setText(getArticleContent());
						setArticleContent("");
					}
					int c = textView.getText().length();
					// Log.i("Reader", "count2="+c);
					countLine = data.getLineCount();
					lineHeight = data.getLineHeight();
					curpos = data.getScrollPosition();
					setProgress((curpos * 1.0f / (countLine * lineHeight)));
					// Log.i("Reader", "append");
					newCount = textView.getLineCount() * textView.getLineHeight();
					// if (newCount == 0) // 这里需要在画完后重新滚动
					// {
					needReScroll = true;
					// } else {
					// scrollView.scrollTo(scrollView.getScrollX(), (int)
					// (getProgress() * newCount));
					// }
					Message msgtmp = new Message();
					msgtmp.what = SET_TEXTVIEW_TEXT_DONE;
					sendMessage(msgtmp);
					break;
				case LOAD_FROM_PRELOADED:
					data = (UserProgressData) msg.obj;
					if (data == null)
					{
						backToContent();
						return;
					}
					articleId = data.getBookId();

					if (getArticleContent() == null || getArticleContent().startsWith("错误")) // 这里取不取内容
					{
						backToContent();
						return;
					}
					novelName = data.getBookName();
					if (novelName == null || novelName.length() == 0)
					{
						novelName = Constant.BOOK_NAME[articleId];
					}
					bookName.setText(novelName);
					textView.setText(getArticleContent());
					int d = textView.getText().length();
					// Log.i("Reader", "count1="+d);
					Configuration.getInstance().setReadingArticle(articleId);
					countLine = data.getLineCount();
					lineHeight = data.getLineHeight();
					curpos = data.getScrollPosition();
					setProgress((curpos * 1.0f / (countLine * lineHeight)));
					// Log.i("Reader", "append");
					newCount = textView.getLineCount() * textView.getLineHeight();

					// if (newCount == 0) // 这里需要在画完后重新滚动
					// {
					needReScroll = true;
					// } else {
					// scrollView.scrollTo(scrollView.getScrollX(),(int)
					// (getProgress() * newCount));
					// }
					// Log.i("Reader", "getLineHeight" +
					// textView.getLineHeight());
					// Log.i("Reader", "getlinecount" +
					// textView.getLineCount());
					// Log.i("Reader", "calu total" + textView.getLineCount() *
					// textView.getLineHeight());
					// Log.i("Reader", "textview hight:" +
					// textView.getHeight());
					Message msgnew = new Message();
					msgnew.what = SET_TEXTVIEW_TEXT_DONE;
					sendMessage(msgnew);
					break;
				case RESCROLL_VIEW:
					newCount = textView.getLineCount() * textView.getLineHeight();
					scrollView.scrollTo(scrollView.getScrollX(), (int) (getProgress() * newCount));
					break;
			}
		}

		private void setProgress(float progress)
		{
			this.progress = progress;
		}

		public float getProgress()
		{
			return progress;
		}
	};
	

	private ProgressDialog loadingDialog;

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		UserProgressData userData = intent.getParcelableExtra("UserProgressData");
		if (userData == null)
		{
			int id = intent.getIntExtra("ARTICLE_ID", 0);
			userData = new UserProgressData();
			userData.setBookId(id);
		}
		browserTo(userData);
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// Log.i("Reader", "handler font");
			switch (msg.what)
			{
				default:
					long drawedLine = textView.getLineCount();
					long newamount = drawedLine * textView.getLineHeight();
					// Log.i("Reader", "newamount" + newamount);
					// Log.i("Reader", "scrollHightBefore" + scrollHightBefore);
					// Log.i("Reader", "currentYBefore" + currentYBefore);
					if (scrollHightBefore != newamount)
					{

						int newY = (int) (newamount * (currentYBefore * 1.0f / scrollHightBefore));
						// Log.i("Reader", "newY" + newY);
						scrollHightBefore = newamount;
						lineCountBefore = drawedLine;
						currentYBefore = newY;
						scrollView.scrollTo(scrollView.getScrollX(), newY);
					}
			}
		}

	};

	private long lineCountBefore;

	private int articleId = 0;

	private LinearLayout rootView;
	private RelativeLayout adLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Log.i("Reader",
		// "onCreate with savedInstanceState="+savedInstanceState);
		onChangeScreen = Tools.setScreenDir(this);
		if (onChangeScreen)
		{
			// Log.i("Reader", "return");
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.readermain);
		intiUIControllor();
		if (getDoc() == null)
		{
			setDoc(new ArticleDocument());
		}
		// if (Configuration.getInstance().isEnableJump()) {
		registerForContextMenu(textView);
		// }
		setInstance(this);
		initMenu();
		UserProgressData upd = getIntent().getParcelableExtra("UserProgressData");

		boolean preload = getIntent().getBooleanExtra(Constant.PRE_LOAD, false);

		if (savedInstanceState != null)
		{
			// Log.i("Reader", "onCreate 2 with savedInstanceState="+
			// savedInstanceState.getParcelable("save"));
			upd = savedInstanceState.getParcelable("save");
			if (upd != null && upd instanceof UserProgressData)
			{
				// Log.i("Reader", "getbookname="+upd.getBookName());
				bookName.setText(upd.getBookName());
				novelName = bookName.getText().toString();
				browserTo(upd);
			} else
			{
				firstLoad();
			}
		}

		else if (preload) // 预加载过的
		{
			// Log.i("Reader", "onCreate 3 withpreload");
			Message msg = new Message();
			msg.what = LOAD_FROM_PRELOADED;
			msg.obj = getIntent().getParcelableExtra(Constant.MARK);
			if (msg.obj != null && msg.obj instanceof UserProgressData)
			{
				bookName.setText(((UserProgressData) msg.obj).getBookName());
				novelName = bookName.getText().toString();
			}
			loadHandler.sendMessage(msg);

		}

		else if (upd != null) // 从书签进来的
		{
			// Log.i("Reader", "onCreate 4 with upd="+ upd);
			if (upd != null && upd instanceof UserProgressData)
			{
				bookName.setText(upd.getBookName());
				novelName = bookName.getText().toString();
			}
			browserTo(upd);
			return;
		} else
		// 从目录进来的
		{
			// Log.i("Reader", "onCreate 5 firstLoad");
			firstLoad();
		}
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getPackageName());
	}

	private void initMenu()
	{
		qqmenu = new CustomerMenu(this, menu_name_array, menu_image_array, rootView);
		qqmenu.LoadMenuRes();

		// qqmenu.getQqMenuDialog().setOnKeyListener(new OnKeyListener() {
		// public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent
		// event) {
		// if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
		// { dialog.dismiss();
		// pausedScroll = false;
		// }
		// return false;
		// }
		// });
		//		
		// qqmenu.getQqMenuDialog().setOnCancelListener(new OnCancelListener() {
		//			
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// pausedScroll = false;
		//				
		// }
		// });
		// qqmenu.getQqMenuDialog().setOnDismissListener(dismissListener);
		/** 监听menu选项 **/
		qqmenu.getQqMenuGridView().setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (qqmenu.getQqMenuDialog().isShowing())
				{
					qqmenu.getQqMenuDialog().dismiss();
				}
				switch (arg2)
				{
					case FONT_SETUP:
						buildFontDialog();
						break;
					case SAVE_MARK:
						saveUserProgress();
						break;
					case LOAD_MARK:
						startActivityForResult(new Intent(ReaderActivity.this, MarkListActivity.class), LOAD_MARK);
						break;
					case ADV_SETUP:
						Intent ii = new Intent();
						ii.setClass(ReaderActivity.this, AdvSetupActivity.class);
						ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivityForResult(ii, ADV_SETUP);
						break;
					case MORE_NOVEL:
						// backToContent();
						Tools.startCategoryActivity(ReaderActivity.this);
						break;
					case LIGHT_SETUP:
						showLightSetup();
						break;
					case EXIT_PROGRAME:
						finish();
						break;
					case SCROLL_SPEED:
						showScollSpeed();

						break;
				}
			}
		});
	}

	/**
	 * 返回目录
	 */
	private void backToContent()
	{
		Intent i = new Intent(ReaderActivity.this, MainMenuActivity.class);
		startActivity(i);
		finish();
	}

	/**
	 * 保存当前进度
	 */
	private void saveUserProgress()
	{
		UserProgressData upData = getCurrentReadProgress();
		upData.setmarkType(UserProgressData.USER_SAVE);
		upData.save();
		makeToast(0, R.string.save_successful, Toast.LENGTH_SHORT, ReaderActivity.this).show();
	}

	/**
	 * 从目录加载
	 */
	private void firstLoad()
	{
		makeToast(0, R.string.setup_waiting, Toast.LENGTH_SHORT, this).show();
		// loadingDialog = ProgressDialog.show(this, "", "加载中，请稍候...", true);
		// loadingDialog.setCancelable(false);
		articleId = getIntent().getIntExtra("ARTICLE_ID", 0);
		novelName = Constant.BOOK_NAME[articleId];
		bookName.setText(novelName);
		// Log.i("Reader", "frist load getbookname="+novelName);
		Thread thread = new Thread()
		{

			public void run()
			{

				String path = Tools.getBookPath(ReaderActivity.this, articleId);
				// Log.i("Reader", "firstload"+path);
				if (!getDoc().openFile(path))
				{
					Tools.copyFile(articleId);
					getDoc().openFile(path);
				}

				Configuration.getInstance().setReadingArticle(articleId);
				setArticleContent(getDoc().getNext());
				// Log.i("Reader", "getNext");
				Message msg = new Message();
				msg.what = GET_TEXT_DONE;
				loadHandler.sendMessageDelayed(msg, 100);
			}
		};
		thread.start();
	}

	private void initUISetting()
	{
		textView.setText("");
		textView.setTextSize(Configuration.getInstance().getFontSize());

		scrollSpeed = Configuration.getInstance().getScrollSpeed();
		disableJumpMenu = Configuration.getInstance().isEnableJump();
	}

	private void intiUIControllor()
	{

		menu_name_array[0] = getString(R.string.save_marked);
		menu_name_array[1] = getString(R.string.font_setup);
		menu_name_array[2] = getString(R.string.advanced_setup);
		menu_name_array[3] = getString(R.string.more_novel);
		menu_name_array[4] = getString(R.string.read_mark);
		menu_name_array[5] = getString(R.string.light_setup);
		menu_name_array[6] = getString(R.string.scroll_speed);
		menu_name_array[7] = getString(R.string.exit_program);
		rootView = (LinearLayout) findViewById(R.id.rootview);
		adLayout = (RelativeLayout) findViewById(R.id.ad_layout);
		View layoutView = findViewById(R.id.textLayout_outside);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (dm.densityDpi < DisplayMetrics.DENSITY_HIGH && Math.min(dm.heightPixels, dm.widthPixels) >= 600)
		{
			layoutView.setBackgroundResource(R.drawable.backgroud_large);
		}

		textView = (ReadView) findViewById(R.id.mainReader);

		textView.setOnDrawListener(new DrawListener()
		{

			@Override
			public void onDrawed()
			{
				int drawedallHight = textView.getLineCount() * textView.getLineHeight();

				if (needReScroll)
				{
					needReScroll = false;
					Message msg = new Message();
					msg.what = RESCROLL_VIEW;
					loadHandler.sendMessage(msg);
				}

				if (drawedallHight != scrollHightBefore && scrollHightBefore != 0)
				{
					handler.sendMessage(new Message());
				}

			}
		});
		textView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// Log.i("Reader", "onTouch");
				return mGestureDetector.onTouchEvent(event);
			}
		});
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					pausedScroll = false;
				} else if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					pausedScroll = true;
				}

				return false;
			}
		});
		// 底边工具栏
		bottomBar = findViewById(R.id.mainmenu_Layout);
		isFullScreen = Configuration.getInstance().isFullScreen();
		if (isFullScreen)
		{
			bottomBar.setVisibility(View.GONE);
		} else
		{
			bottomBar.setVisibility(View.VISIBLE);
		}

		Button menuTouch = (Button) findViewById(R.id.menu_touch);
		menuTouch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				ReaderActivity.this.openOptionsMenu();
			}
		});

		// 书名
		bookName = (Button) findViewById(R.id.article_title);
		bookName.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				saveUserProgress();
			}
		});

		// 跳转
		Button jump = (Button) findViewById(R.id.article_jump_next);
		jump.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				jumpToNext();
			}
		});
		// 滚屏
		Button scrollScreen = (Button) findViewById(R.id.article_scroll);
		scrollScreen.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				autoScroll((TextView) v);
			}
		});

		initGestureEvent();
		// Log.i("Reader", "getLineHeight" + textView.getLineHeight());
		//
		GlobalDataManager.getInstance().getPreferences().registerOnSharedPreferenceChangeListener(spChangeListener);
		initUISetting();
		initAD();
	}

	/**
	 * 跳至下一章
	 */
	protected void jumpToNext()
	{
		if (articleId == Constant.FILE_LIST.length - 1)
		{
//			makeToast(0, R.string.already_last, Toast.LENGTH_SHORT, this).show();
			AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
			alertbuilder.setMessage(R.string.next_not_available);
			alertbuilder.setTitle(R.string.a_tips);
			final AlertDialog alert = alertbuilder.create();
			alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.try_it), new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					
					alert.dismiss();
					Intent i = new Intent(ReaderActivity.this, CategoryActivity.class);
					startActivity(i);
				}
			});
			alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no_try), new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					
					alert.dismiss();
				
				}
			});
			alert.show();
			return;
		}
		int articleIdToJump = articleId + 1;
		UserProgressData userData = UserProgressManager.getBooKProgress(articleIdToJump);
		if (userData == null)
		{
			userData = new UserProgressData();
			userData.setBookId(articleIdToJump);
		}

		browserTo(userData);
	}

	/**
	 * 跳至上一章
	 */
	protected void jumpToPrev()
	{
		if (articleId == 0)
		{
			makeToast(0, R.string.already_first, Toast.LENGTH_SHORT, this).show();
			return;
		}
		int articleIdToJump = articleId - 1;
		UserProgressData userData = UserProgressManager.getBooKProgress(articleIdToJump);
		if (userData == null)
		{
			userData = new UserProgressData();
			userData.setBookId(articleIdToJump);
		}

		browserTo(userData);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.string.jump_to:
				showJumpToDialog();
				break;
			case R.string.turn_down:
				turnPageDown();
				break;
			case R.string.turn_up:
				turnPageUp();
				break;
			case R.string.jump_to_begin:
				scrollView.scrollTo(scrollView.getScrollX(), 0);
				break;
			case R.string.jump_to_end:
				scrollView.scrollTo(scrollView.getScrollX(), textView.getHeight());
				break;
			case R.string.to_next:
				jumpToNext();
				break;
			case R.string.to_prev:
				jumpToPrev();
				break;
			case R.string.system_menu:
				openOptionsMenu();
				break;
			case R.string.scroll_screen: // 这两个case用同一个语句
			case R.string.scroll_screen_stop:
				Button scrollScreen = (Button) findViewById(R.id.article_scroll);
				autoScroll(scrollScreen);
				break;
		}
		return super.onContextItemSelected(item);
	}

	private void showJumpToDialog()
	{
		userDataBeforeJump = getCurrentReadProgress(); // 获取当前进度
		pausedScroll = true;
		LayoutInflater factory = LayoutInflater.from(this);
		textEntryView = factory.inflate(R.layout.jump_to_dialog, null);
		TextView et = (TextView) textEntryView.findViewById(R.id.user_jump);
		int height = textView.getHeight();
		int cy = scrollView.getScrollY();

		SeekBar seekbar = (SeekBar) textEntryView.findViewById(R.id.seekbar);
		seekbar.setMax(100);

		seekbar.setProgress((int) Math.round(cy * 100.0 / (height - scrollView.getHeight())));

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				jumpTo(progress);
				TextView et = (TextView) textEntryView.findViewById(R.id.user_jump);
				et.setText(String.valueOf(progress) + "%");

			}
		});
		Button add = (Button) textEntryView.findViewById(R.id.plus);
		add.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SeekBar seekbar = (SeekBar) textEntryView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(1);
			}
		});

		Button sub = (Button) textEntryView.findViewById(R.id.minus);
		sub.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SeekBar seekbar = (SeekBar) textEntryView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(-1);
			}
		});

		et.setText(seekbar.getProgress() + "%");

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.jump_to).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// TextView et = (TextView)
				// textEntryView.findViewById(R.id.user_jump);
				// try {
				// float jump =
				// Float.parseFloat(et.getText().toString());
				// if (jump >= 0 && jump <= 100) {
				// jumpTo(jump);
				// } else {
				// // Toast.makeText(ReaderActivity.this,
				// "输入有误，请输入0到100之间的数字", Toast.LENGTH_LONG).show();
				// Log.i("Reader", "jump error" + jump);
				// }
				// } catch (Exception e) {
				// // Toast.makeText(ReaderActivity.this,
				// "输入有误，请输入0到100之间的数字", Toast.LENGTH_LONG).show();
				// Log.i("Reader", "jump ex" );
				// }
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				jumpLocalTo(userDataBeforeJump);
				dialog.cancel();
			}
		}).setView(textEntryView).setCancelable(false);

		AlertDialog ad = builder.create();
		ad.setOnDismissListener(dismissListener);
		ad.show();
	}

	/**
	 * 跳至
	 * 
	 * @param i
	 *            %
	 */
	protected void jumpTo(int i)
	{
		// TODO Auto-generated method stub
		long target = i;
		int height = textView.getHeight();
		int jumpTo = (int) ((height - scrollView.getHeight()) * target / 100);
		scrollView.smoothScrollTo(scrollView.getScrollX(), jumpTo);
	}

	private void jumpLocalTo(UserProgressData data)
	{
		scrollView.smoothScrollTo(scrollView.getScrollX(), data.getScrollPosition());
	}

	/**
	 * 向下翻页
	 */
	private void turnPageDown()
	{
		int y = scrollView.getScrollY();
		int height = scrollView.getHeight();
		scrollView.smoothScrollTo(scrollView.getScrollX(), y + height - Constant.TURN_PAGE_FIX);
	}

	/**
	 * 向上翻页
	 */
	private void turnPageUp()
	{
		int y = scrollView.getScrollY();
		int height = scrollView.getHeight();
		scrollView.smoothScrollTo(scrollView.getScrollX(), y - height + Constant.TURN_PAGE_FIX);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		if (Configuration.getInstance().isEnableJump())
		{
			// menu.add(Menu.NONE, R.string.turn_down, 0, R.string.turn_down);
			// menu.add(Menu.NONE, R.string.turn_up, 1, R.string.turn_up);
			menu.add(Menu.NONE, R.string.jump_to, 2, R.string.jump_to);
			menu.add(Menu.NONE, R.string.jump_to_begin, 3, R.string.jump_to_begin);
			menu.add(Menu.NONE, R.string.jump_to_end, 4, R.string.jump_to_end);
			if (articleId != 0) // 第一章
			{
				menu.add(Menu.NONE, R.string.to_prev, 4, R.string.to_prev);
			}
			if (articleId != Constant.FILE_LIST.length - 1) // 最后一章
			{
				menu.add(Menu.NONE, R.string.to_next, 4, R.string.to_next);
			}
			// getMenuInflater().inflate(R.menu.reader_menu, menu);
			// menu.setHeaderIcon(android.R.drawable.ic_dialog_info);
			menu.setHeaderTitle(R.string.jump_menu);
			// Log.i("Reader", "onCreateContextMenu" + menu);
			if (Configuration.getInstance().isFullScreen())// 如果全屏才加入菜单
			{

				menu.add(Menu.NONE, R.string.system_menu, 10, R.string.system_menu);
			}
			if (isAutoScroll && scrollSpeed != 0)
			{
				menu.add(Menu.NONE, R.string.scroll_screen_stop, 9, R.string.scroll_screen_stop);
			} else
			{
				menu.add(Menu.NONE, R.string.scroll_screen, 9, R.string.scroll_screen);
			}
			pausedScroll = true;
		}
	}

	@Override
	public void onContextMenuClosed(Menu menu)
	{

		super.onContextMenuClosed(menu);
		pausedScroll = false;
	}

	@Override
	protected void onDestroy()
	{
		// 
		GlobalDataManager.getInstance().getPreferences().unregisterOnSharedPreferenceChangeListener(spChangeListener);
		isAutoScroll = false;
		// if (Configuration.getInstance().isEnableJump()) {
		if (scrollView != null)
		{
			unregisterForContextMenu(scrollView);
		}
		showTimeHandler.removeMessages(0);
		// }
		// AdKnotsLayout.finish(this);
		if( adModView!=null)
		{
			adModView.destroy();
		}
		
		super.onDestroy();
		getDoc().closeFile();
		// Log.i("Reader", "onDesotry");
	}

	@Override
	/**
	 * 创建MENU
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * 拦截MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		qqmenu.showQqMenuDialog();
		pausedScroll = true; // 暂停
		return false;// 返回为true 则显示系统menu
	}

	private void lockScreen()
	{
		if (wl != null && !wl.isHeld())
		{
			wl.acquire();
		}
	}

	private void unlockScreen()
	{
		if (wl != null && wl.isHeld())
		{
			wl.release();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
			case LOAD_MARK:
				if (data == null)
				{
					break;
				}
				long bookID = data.getLongExtra("markID", -1);
				if (bookID == -1)
				{
					break;
					// do nothing
				}
				UserProgressData ud = UserProgressManager.getProgress(bookID);
				browserTo(ud);
				break;
			case ADV_SETUP:
				// Log.i("Reader", "ADV_SETUP");
				if (resultCode == 1)
				{
					// Log.i("Reader", "ADV_SETUP" + "browserTo");
					browserTo(UserProgressManager.getAutoProgress());
				}
		}
		return;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		// savedInstanceState.putParcelable("save", getCurrentReadProgress());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if (textView != null)
		{
			outState.putParcelable("save", getCurrentReadProgress());
		} else
		{
			UserProgressData upd = getIntent().getParcelableExtra("UserProgressData");
			if (upd != null)
			{
				outState.putParcelable("save", upd);
			} else
			{

				outState.putParcelable("intent", getIntent());
			}

		}
	}

	private void autoScroll(TextView v)
	{
		isAutoScroll = !isAutoScroll;
		if (isAutoScroll && scrollSpeed != 0)
		{
			v.setText(getResources().getString(R.string.scroll_screen_stop));
			scrollHandler.sendMessageDelayed(new Message(), scrollSpeed);
			lockScreen();
		} else
		{
			v.setText(getResources().getString(R.string.scroll_screen));
			unlockScreen();
		}
	}

	private Handler scrollHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (!pausedScroll)
			{
				int x = scrollView.getScrollX();
				int y = scrollView.getScrollY();

				y = y + 4;

				scrollView.scrollTo(x, y);
			}
			if (isAutoScroll && scrollSpeed != 0)
			{
				sendMessageDelayed(new Message(), scrollSpeed);
			}
		}
	};

	protected boolean bScreenChanged = false;

	private UserProgressData getCurrentReadProgress()
	{
		if (textView == null)
		{
			return null;
		}
		UserProgressData upData = new UserProgressData();
		upData.setLineCount(textView.getLineCount());
		upData.setLineHeight(textView.getLineHeight());
		upData.setBookName(Constant.BOOK_NAME[articleId]);
		// Log.i("Reader", "save 0-91"+getDoc().getFilePath());
		// Log.i("Reader", "articleId:"+articleId);
		// upData.setBookPath(getDoc().getFilePath());
		upData.setScrollPosition(scrollView.getScrollY());
		upData.setBookId(articleId);
		upData.setSaveDate(System.currentTimeMillis());
		return upData;
	}

	private void onTextViewRefresh()
	{
		scrollHightBefore = textView.getLineCount() * textView.getLineHeight();
		currentYBefore = scrollView.getScrollY();
		// Log.i("Reader", "total amount before change font:" +
		// scrollHightBefore);

		// Log.i("Reader", "current Y  before change font:" + currentYBefore);
		lineCountBefore = textView.getLineCount();
		// Log.i("Reader", "lineCount before change font:" + lineCountBefore);
		// int scrollHight2 = scrollView.getMaxScrollAmount();
		// int currentY2 = scrollHight2 * currentY / scrollHighted ;
		// Log.i("Reader", "total amount after change font:" + scrollHighted);
		// Log.i("Reader", "current Y  after change font:" + currentY2);
		// int lineCount2 = textView.getLineCount();
		// Log.i("Reader", "lineCount2  after change font:" + lineCount2);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub

		// MenuInflater mi = getMenuInflater();
		// mi.inflate(R.menu.reader_menu, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * 跳到书签位置浏览
	 * 
	 * @param data
	 * @return
	 */
	private boolean browserTo(UserProgressData data)
	{
		// Log.i("Reader", "browserTo"+data);
		if (data == null)
		{
			return false;
		}
		makeToast(0, R.string.setup_waiting, Toast.LENGTH_SHORT, this).show();
		// Log.i("Reader", "UserProgressData"+ data);
		// loadingDialog = ProgressDialog.show(this, "", "加载中，请稍候...", true);
		// loadingDialog.setCancelable(false);
		// bookName.setText(data.getBookName());
		Message msg = new Message();
		msg.what = LOAD_FROM_MARK;
		msg.obj = data;
		loadHandler.sendMessageDelayed(msg, 100);
		return true;
	}

	public static void setArticleContent(String articleContent)
	{
		ReaderActivity.articleContent = articleContent;
	}

	public static String getArticleContent()
	{
		return articleContent;
	}

	static void setDoc(ArticleDocument doc)
	{
		ReaderActivity.doc = doc;
	}

	static ArticleDocument getDoc()
	{
		if (doc == null)
		{
			doc = new ArticleDocument();
		}
		return doc;
	}

	private OnSharedPreferenceChangeListener spChangeListener = new OnSharedPreferenceChangeListener()
	{

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			// Log.i("Reader", "onSharedPreferenceChanged2" + key);
			if (key.equalsIgnoreCase(Configuration.KEY_SCROLL_SPEED))
			{
				scrollSpeed = sharedPreferences.getInt(Configuration.KEY_SCROLL_SPEED, Configuration.getInstance().getScrollSpeed());
			} else if (key.equalsIgnoreCase(Configuration.KEY_LINE_HEIGHT))
			{
				int lineHight = sharedPreferences.getInt(Configuration.KEY_LINE_HEIGHT, (Configuration.getInstance().getLineHeight()));
				onTextViewRefresh();
				textView.setLineSpacing(0, (float) (lineHight / 10.0));

			} else if (key.equalsIgnoreCase(Configuration.KEY_FONTSIZE))
			{
				int fontSize = sharedPreferences.getInt(Configuration.KEY_FONTSIZE, (Configuration.getInstance().getFontSize()));
				onTextViewRefresh();
				textView.setTextSize(fontSize);

			} else if (key.equalsIgnoreCase(Configuration.KEY_DISABLE_JUMP))
			{
				disableJumpMenu = sharedPreferences.getBoolean(Configuration.KEY_DISABLE_JUMP, (Configuration.getInstance().isEnableJump()));
				// if (disableJumpMenu) {
				// unregisterForContextMenu(textView);
				// } else {
				// registerForContextMenu(textView);
				// }
			} else if (key.equalsIgnoreCase(Configuration.KEY_FULL_SCREEN))
			{
				isFullScreen = sharedPreferences.getBoolean(Configuration.KEY_FULL_SCREEN, false);
				if (isFullScreen)
				{
					bottomBar.setVisibility(View.GONE);
				} else
				{
					bottomBar.setVisibility(View.VISIBLE);
				}
			} else if (key.equalsIgnoreCase(Configuration.KEY_LAND_PORT))
			{
				// Log.d("Reader",
				// Configuration.KEY_LAND_PORT+":"+Configuration.getInstance().getLandPort());
				bScreenChanged = true;
				// if( Configuration.getInstance().isLand())
				// {
				// ReaderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				// }
				// else
				// {
				// ReaderActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// }
			}
		}
	};

	private View textEntryView;

	private GestureDetector mGestureDetector;

	private PowerManager.WakeLock wl;

	private AlertDialog seekDialog;

	private View seekView;

	private View bottomBar;

	private TextView bookName;

	private void initGestureEvent()
	{
		mGestureDetector = null;

		mGestureDetector = new GestureDetector(this, new OnGestureListener()
		{

			public boolean onDown(MotionEvent e)
			{
				// TODO Auto-generated method stub
				// Log.d("Reader", "onDown");
				return false;
			}

			// 参数解释：
			// e1：第1个ACTION_DOWN MotionEvent
			// e2：最后一个ACTION_MOVE MotionEvent
			// velocityX：X轴上的移动速度，像素/秒
			// velocityY：Y轴上的移动速度，像素/秒
			// 触发条件 ：
			// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				// TODO Auto-generated method stub
				// Log.i("Reader", " onFling e1:" + e1 + "e2:" + e2);
				if (velocityY * velocityY * 5 > velocityX * velocityX || e1 == null || e2 == null)
					return false;

				final int FLING_MIN_DISTANCE = 30, FLING_MIN_VELOCITY = 30;
				if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)
				{
					// Fling left
					// Log.d(LOGNAME, "Fling left");
					// 返回true就仅仅在重载的方法里处理，不会调用它的超类方法
					// titleTab.switchItem(false);
					turnPageUp();
					return true;
				} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)
				{
					// Fling right
					// Log.d(LOGNAME, "Fling right");
					turnPageDown();

					return true;
				}
				return false;

			}

			//
			@Override
			public void onLongPress(MotionEvent e)
			{
				// TODO Auto-generated method stub
				// Log.d("Reader", "onLongPress");
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				// Log.i("Reader"," onScroll e1:"+e1 + "e2:"+e2);
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e)
			{
				// TODO Auto-generated method stub
				// Log.d("Reader", "onShowPress");
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				// TODO Auto-generated method stub
				// Log.d("Reader", "onSingleTapUp");
				return false;
			}

		});
	}

	private static void setInstance(ReaderActivity instance)
	{
		ReaderActivity.instance = instance;
	}

	public static ReaderActivity getInstance()
	{
		return instance;
	}

	private void buildSeeKDialog(String title, String imply, DialogInterface.OnClickListener okListener,
			DialogInterface.OnClickListener cancelListener, DialogInterface.OnCancelListener onCancelListener, OnSeekBarChangeListener seekListener,
			int seekMax, int seekInitPos)
	{
		pausedScroll = true;
		LayoutInflater factory = LayoutInflater.from(this);
		seekView = factory.inflate(R.layout.dialog_size, null);
		seekDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle(title).setView(seekView).setPositiveButton(
				R.string.ok, okListener).setNegativeButton(R.string.cancel, cancelListener).setOnCancelListener(onCancelListener).create();

		SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
		seekbar.setMax(seekMax);

		seekbar.setProgress(seekInitPos);

		seekbar.setOnSeekBarChangeListener(seekListener);

		Button add = (Button) seekView.findViewById(R.id.plus);
		add.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(10);
			}
		});

		Button sub = (Button) seekView.findViewById(R.id.minus);
		sub.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(-10);
			}
		});

		TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
		tv.setText(String.valueOf(seekInitPos));
		TextView im = (TextView) seekView.findViewById(R.id.imply);
		seekDialog.setOnDismissListener(dismissListener);
		im.setText(imply);
		seekDialog.show();

	}

	private void buildFontDialog()
	{
		pausedScroll = true;
		LayoutInflater factory = LayoutInflater.from(this);
		final View fontDialogView = factory.inflate(R.layout.dialog_font_size, null);
		seekDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle("字体设置").setView(fontDialogView)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{

						SeekBar seekbar = (SeekBar) fontDialogView.findViewById(R.id.font_sekkbar);
						int position = seekbar.getProgress();
						seekDialog.dismiss();
						setFont(caculateFontSize(position));

					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{

						seekDialog.dismiss();
					}
				}).create();

		SeekBar seekbar = (SeekBar) fontDialogView.findViewById(R.id.font_sekkbar);
		seekbar.setMax(15);
		int currentFont = Configuration.getInstance().getFontSize();
		seekbar.setProgress((currentFont - 12) / 2);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) fontDialogView.findViewById(R.id.font_switcher);
				tv.setTextSize(caculateFontSize(progress));
				tv.setText(caculateFontSize(progress) + "号字体");
			}
		});

		TextView tv = (TextView) fontDialogView.findViewById(R.id.font_switcher);
		tv.setTextSize(currentFont);
		tv.setText(currentFont + "号字体");
		seekDialog.setOnDismissListener(dismissListener);
		seekDialog.show();

	}

	private int caculateFontSize(int position)
	{
		return position * 2 + 12;
	}

	private void setFont(int fontsize)
	{
		Configuration.getInstance().setFontSize(fontsize);
	}

	private void showAbout()
	{
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.about, null);
		AlertDialog alert = new AlertDialog.Builder(this).setTitle(R.string.set_about).setView(textEntryView).create();
		alert.show();
	}

	private int getScreenLight()
	{
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		oldScreenLight = (int) (lp.screenBrightness * 255);
		return oldScreenLight;
	}

	/**
	 * 设置滚动速度
	 */
	private void showScollSpeed()
	{
		buildSeeKDialog(getResources().getString(R.string.set_scroll_speed), getResources().getString(R.string.fast_slow),

		new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SeekBar bar = (SeekBar) seekView.findViewById(R.id.seekbar);
				int position = bar.getProgress();
				// 40-440之间
				scrollSpeed = Constant.MAX_SCROLL_SPEED - position;
				Configuration.getInstance().setScrollSpeed(scrollSpeed);

				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		}, null, new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
				tv.setText(String.valueOf(progress));
			}
		}, Constant.MAX_SCROLL_SPEED - Constant.MIN_SCROLL_SPEED, Constant.MAX_SCROLL_SPEED - scrollSpeed);
	}

	/**
	 * 设置屏幕亮度
	 */
	private void showLightSetup()
	{
		oldScreenLight = getScreenLight();
		int brightness = oldScreenLight;
		if (oldScreenLight < 0)
		{

			try
			{
				brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
				// Log.d("Reader", "brightness" + brightness);
			} catch (Exception e)
			{
				brightness = -255;
			}

			if (brightness < 0)
			{
				brightness = 125;
			}

		}
		buildSeeKDialog(getResources().getString(R.string.set_light), getResources().getString(R.string.dark_light),

		new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SeekBar bar = (SeekBar) seekView.findViewById(R.id.seekbar);
				int position = bar.getProgress();
				// 0-255之间
				setScreenLight(position);
				Configuration.getInstance().setScreenLight(position);
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				setScreenLight(oldScreenLight);
				dialog.dismiss();
			}
		}, new DialogInterface.OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				setScreenLight(oldScreenLight);
			}
		}, new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
				tv.setText(String.valueOf(progress));
				setScreenLight(progress);
			}
		}, 255, brightness);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			backToContent();
			return true;
		}
		// else if( keyCode == KeyEvent.KEYCODE_MENU)
		// {
		// if( qqmenu.isShowing())
		// {
		// qqmenu.dismiss();
		// return true;
		// }
		// }
		return super.onKeyDown(keyCode, event);
	}

	// @Override
	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// if( keyCode == KeyEvent.KEYCODE_MENU)
	// {
	// if( qqmenu.isShowing())
	// {
	// qqmenu.dismiss();
	// return true;
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private DialogInterface.OnDismissListener dismissListener = new OnDismissListener()
	{

		@Override
		public void onDismiss(DialogInterface dialog)
		{
			pausedScroll = false;

		}
	};

	private com.google.ads.AdView adModView;

//	private net.youmi.android.AdView adModView;

	private com.madhouse.android.ads.AdView madAd;

	/**
	 * 广告逻辑
	 */
	private void initAD()
	{
		// AdKnotsTargeting.setAge(23);
		// AdKnotsTargeting.setGender(Gender.MALE);
		// AdKnotsTargeting.setPostalCode(Constant.SMARTMAD_BANNERID);
		// AdKnotsTargeting.setTestMode(false);
		//	        
		// 有米
		if (Constant.ADMOB_SUPPORT)
		{
	
//			adModView = new com.google.ads.AdView(this, AdSize.IAB_BANNER, Constant.ADMOB_ID);
			adModView = (com.google.ads.AdView)findViewById(R.id.admobView);
//			RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			adModView.setAdListener(admobListener);
//			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//			AdRequest adRequest = new AdRequest();
			adModView.loadAd(new AdRequest());
			
//			adModView.ad
//			adLayout.addView(adModView, lp);
//			adModView.setVisibility(View.VISIBLE);
		}

		if (Constant.SMARTMAD_SUPPORT)
		{
			RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			madAd = new com.madhouse.android.ads.AdView(this, null, 0, Constant.SMARTMAD_BANNERID, 30, 0, false);
			lp = new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			madAd.setListener(smartadListener);
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			madAd.setVisibility(View.GONE);
			adLayout.addView(madAd, lp);
		}
//		long adMobTime = DynamicConfigure.getInstance().getAdmobTime();
//		long smartAdtime = DynamicConfigure.getInstance().getSmartmadTime();
		if( Constant.SMARTMAD_SUPPORT && Constant.ADMOB_SUPPORT)
		{
			int rand = ((new Random()).nextInt())%2;
			if( rand == 2)
			{
				if( madAd != null)
				{
					madAd.setVisibility(View.VISIBLE);
				}
				
			}
			else
			{
				if( adModView != null)
				{
					adModView.setVisibility(View.VISIBLE);
				}
			}
			
			showTimeHandler.sendEmptyMessageDelayed(0, 1000 * 10);
			
		}
		else if( Constant.SMARTMAD_SUPPORT)
		{
			if( madAd != null)
			{
				madAd.setVisibility(View.VISIBLE);
			}
		}
		else  if( Constant.ADMOB_SUPPORT)
		{
			if( adModView != null)
			{
		
				adModView.setVisibility(View.VISIBLE);
			}
		}
		
		// AdKnotsLayout adKnotsLayout = new AdKnotsLayout(this,
		// Constant.SMARTMAD_ID);
		//	        
		// adKnotsLayout.setAdKnotsInterface(this);
		// LinearLayout.LayoutParams adKnotsLayoutParams = new
		// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// rootView.addView(adKnotsLayout, adKnotsLayoutParams);

		rootView.invalidate();

		// adview = (AdView) findViewById(R.id.ad);
		// // adview.set
		// adview.setOnTouchListener(new OnTouchListener() {
		//			
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// Log.i("Reader", "onTouch AD");
		// if( Configuration.getInstance().isHideAD())
		// {
		// adview.setVisibility(View.GONE);
		// adHandler.sendEmptyMessageDelayed(0, Constant.AD_TIMER);
		// }
		// return false;
		// }
		// });
		// adview.setOnClickListener(new OnClickListener() {
		//			
		// @Override
		// public void onClick(View v) {
		// Log.i("Reader", "onClick AD");
		// if( Configuration.getInstance().isHideAD())
		// {
		// adview.setVisibility(View.GONE);
		// adHandler.sendEmptyMessageDelayed(0, Constant.AD_TIMER);
		// }
		//				
		// }
		// });
	}

	private boolean admobAvaiable = false;
	
	private boolean smartAdAvaiable = false;
	
	private com.google.ads.AdListener admobListener = new com.google.ads.AdListener()
	{
		
		@Override
		public void onReceiveAd(Ad arg0)
		{
			Log.v("Test", "onReceiveAd:"+arg0);
			if( !admobAvaiable )
			{
				admobAvaiable = true;
			}
			
		}
		
		@Override
		public void onPresentScreen(Ad arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLeaveApplication(Ad arg0)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1)
		{
			Log.v("Test", "onFailedToReceiveAd:"+arg0);
			
		}
		
		@Override
		public void onDismissScreen(Ad arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};
	
	private com.madhouse.android.ads.AdListener smartadListener = new com.madhouse.android.ads.AdListener()
	{


		@Override
		public void onAdEvent(com.madhouse.android.ads.AdView arg0, int arg1)
		{
			if (smartAdAvaiable == false && arg1 == 200)
			{
				smartAdAvaiable = true;
				// madAd.setVisibility(View.VISIBLE);
				// Log.v("Test", "smartadavalibe = "+smartAdAvaiable);
			}

		}

		@Override
		public void onAdFullScreenStatus(boolean arg0)
		{
			// TODO Auto-generated method stub
			
		}
	};

	
	private boolean showingTime = false;
	private Handler showTimeHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			long admobTime = DynamicConfigure.getInstance().getAdmobTime();
			long smartAdtime = DynamicConfigure.getInstance().getSmartmadTime();

			// int h = adModView.getAdHeight();
			// Log.d("Test", "youmi height"+h);
			if (smartAdtime == 0 && admobTime == 0) // 如果都没广告
			{
				adLayout.setVisibility(View.GONE);
				return;
			} else
			{
				
				if( adModView != null)
				{
					AdRequest adRequest = new AdRequest();
					adModView.loadAd(adRequest);
				}
				
				if (madAd != null && madAd.getVisibility() == View.VISIBLE  && admobAvaiable && Constant.ADMOB_SUPPORT)
				{
					madAd.setVisibility(View.INVISIBLE);
					if (adModView != null)
					{
						adModView.setVisibility(View.VISIBLE);
					}
					sendEmptyMessageDelayed(0, admobTime);
				} else if (adModView != null && adModView.getVisibility() == View.VISIBLE  && smartAdAvaiable
						&& Constant.SMARTMAD_SUPPORT)
				{
					adModView.setVisibility(View.INVISIBLE);
					if (madAd != null)
					{
						madAd.setVisibility(View.VISIBLE);
						madAd.invalidate();
					}
					sendEmptyMessageDelayed(0, smartAdtime);
				}
				else
				{
					sendEmptyMessageDelayed(0, 1000*10);
				}
			}

			showingTime = true;
			
		}
	};
}