package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseStore extends SQLiteOpenHelper {
    private static final String TRANSACTIONS = "transactions";
    private static final String ACCOUNTS = "accounts";

    public DatabaseStore(Context context) {
        super(context, "180488B.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTransactionTableStatement = "CREATE TABLE " + TRANSACTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT," +
                "accountNo STRING," +
                "expenseType STRING," +
                "amount REAL)";
        db.execSQL(createTransactionTableStatement);
        String createAccountsTableStatement = "CREATE TABLE " + ACCOUNTS + " (" +
                "accountNo STRING PRIMARY KEY," +
                "bankName STRING," +
                "accountHolderName STRING," +
                "balance REAL)";
        db.execSQL(createAccountsTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
