package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO extends DatabaseStore implements TransactionDAO {
    public static final String TRANSACTIONS = "transactions";
    private final List<Transaction> transactions;

    public PersistentTransactionDAO(Context context) {
        super(context);
        transactions = getFromDB();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

        cv.put("date", dateFormat.format(date));
        cv.put("accountNo", accountNo);
        cv.put("expenseType", expenseType.toString());
        cv.put("amount", amount);

        long insert = db.insert(TRANSACTIONS, null, cv);
        if( insert == -1) {
            //return false;
        }else{
            //
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

    public List<Transaction> getFromDB () {
        List<Transaction> transactions = new LinkedList<>();
        String queryString = "SELECT * FROM " + TRANSACTIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery(queryString, null);
        if(result.moveToFirst()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
            do {
                Date date = null;
                try {
                    date = dateFormat.parse( result.getString(1) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accountNo = result.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf( result.getString(3).toUpperCase() );
                double amount = result.getDouble(4);

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);

            } while ( result.moveToNext() );
        } else {

        }
        result.close();
        db.close();

        return transactions;
    }
}
