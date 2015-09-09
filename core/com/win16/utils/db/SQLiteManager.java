package com.win16.utils.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SQLiteManager{
	private static final String tag = "SQLiteManager";

	//宸茬粡璋冪敤杩噊nConnected()鏂规硶鐨勭被
	private static Map<Class<TableData>,Class<TableData>> checkedSet = new ConcurrentHashMap<Class<TableData>,Class<TableData>>();
    /**
     * 鏁版嵁搴撶殑鍞竴瀹炰緥
     */
    private static QQDbOpenHelper helper;
    private static Context context;
    
    //浣跨敤鍓嶅簲璇ラ鍏堣缃笂涓嬫枃
    public static void setContext(Context context){
    	SQLiteManager.context = context;
    }
    
	
    public static SQLiteDatabase getWritableDatabase() {
        if (helper == null) {
        	if(context == null){
        		throw new RuntimeException("context not set");
        	}
        	else{
        		helper = new QQDbOpenHelper(context);
        	}
        }
        return helper.getWritableDatabase();
    }

    
    //TODO QQ閫�嚭鏃惰皟鐢�   
    public static void closeDatabase() {
        if (helper != null) {
        	helper.close();
        	helper = null;
        }
    }
    
    /**
     * 
     * 琛ㄧ粨鏋勬鏌�
     * 
     * @param data
     */
    public static void checkStructure(TableData data){
    	Class cls = data.getClass();
    	if(!checkedSet.containsKey(cls)){
    		data.checkStructure(getWritableDatabase());
    		checkedSet.put(cls, cls);
    	}    	
    }

    /**
     * 
     * @param data
     * @return
     */
    public static long add(TableData data){
    	//琛ㄦ鏌�
    	checkStructure(data);
    	
//    	QLog.i(tag,"save("+data+")");
    	return data.insertTo(getWritableDatabase());
    }
    
    /**
     * 鏌ヨ鏁版嵁
     * 
     * @param oriTableData
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */    
	public static List<TableData> query(TableData oriTableData,String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having,
            String orderBy,String limit){		
		SQLiteManager.checkStructure(oriTableData);
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		List<TableData> result = new ArrayList<TableData>();
		Cursor cur = db.query( table,  columns,  selection,
	             selectionArgs,  groupBy,  having, orderBy, limit);
		if(cur.getCount() > 0){
			cur.moveToFirst();
			do{
				TableData tableData = oriTableData.readFrom(cur);
				result.add(tableData);
			}
			while(cur.moveToNext());
		}
		cur.close();
		return result;
	}
	
	/**
	 * 鏌ヨ涓�釜瀵硅薄
	 * 
	 * @param oriTableData
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public static TableData querySingle(TableData oriTableData,String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having,
            String orderBy,String limit){		
		List<TableData> result = SQLiteManager.query(oriTableData, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		if(result != null && result.size() > 0){
			return result.get(0);
		}
		else{
			return null;
		}
	}

	/**
	 * 
	 * @param oriTableData
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static TableData querySingle(TableData oriTableData,String table, String[] columns, String selection,
            String[] selectionArgs){		
		return SQLiteManager.querySingle(oriTableData, table, columns, selection, selectionArgs, null, null, null, null);
	}
	
	/**
	 * 
	 * @param oriTableData
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return 鍙楀奖鍝嶇殑璁板綍鏁�
	 */
	public static int delete(TableData oriTableData,String table, String whereClause, String[] whereArgs){		
		SQLiteManager.checkStructure(oriTableData);
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		int result = db.delete(table, whereClause, whereArgs);
		return result;
	}
	
	
	/**
	 * 
	 * @param oriTableData
	 * @param table
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return 鍙楀奖鍝嶇殑璁板綍鏁�
	 */
	public static int update(TableData oriTableData,String table, ContentValues values, String whereClause, String[] whereArgs){		
		SQLiteManager.checkStructure(oriTableData);
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		int result = db.update(table, values, whereClause, whereArgs);
		return result;
	}
	
	/**
	 * 
	 * @param oriTableData
	 * @param table
	 * @param nullColumnHack
	 * @param initialValues
	 * @return 鏂版彃鍏ョ殑id
	 */
	public static long replace(TableData oriTableData,String table, String nullColumnHack, ContentValues initialValues){		
		SQLiteManager.checkStructure(oriTableData);
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		long result = db.replace(table, nullColumnHack, initialValues);
		return result;
	}
	

    
	
}