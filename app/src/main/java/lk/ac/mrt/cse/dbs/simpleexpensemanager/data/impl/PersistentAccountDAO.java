package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ui.MainActivity;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class PersistentAccountDAO extends DatabaseStore implements AccountDAO, Serializable {
    public static final String ACCOUNTS = "accounts";
    private final Map<String, Account> accounts;

    public PersistentAccountDAO(@Nullable Context context) {
        super(context);
        //this.accounts = new HashMap<>();
        this.accounts = getFromDB();
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        accounts.put(account.getAccountNo(), account);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("accountNo", account.getAccountNo());
        cv.put("bankName", account.getBankName());
        cv.put("accountHolderName", account.getAccountHolderName());
        cv.put("balance", account.getBalance());

        long insert = db.insert(ACCOUNTS, null, cv);
        if( insert == -1) {

            //return false;
        }else{
            //
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);

        SQLiteDatabase db = this.getWritableDatabase();
        String delQuery = "DELETE FROM " + ACCOUNTS + " WHERE accountNo = " + accountNo;
        db.execSQL(delQuery);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);
    }



    public Map<String, Account> getFromDB () {
        Map<String, Account> accounts = new HashMap<>();
        String queryString = "SELECT * FROM " + ACCOUNTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery(queryString, null);
        if(result.moveToFirst()) {
            do {
                String accountNo = result.getString(0);
                String bankName = result.getString(1);
                String accountHolderName = result.getString(2);
                double balance = result.getDouble(3);

                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accounts.put(account.getAccountNo(), account);

            } while ( result.moveToNext() );
        } else {

        }
        result.close();
        db.close();

        return accounts;
    }
}
