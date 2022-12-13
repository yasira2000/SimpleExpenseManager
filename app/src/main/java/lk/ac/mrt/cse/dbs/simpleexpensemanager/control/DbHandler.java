package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DbHandler extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DB_NAME = "200480X";
    private static final String TABLE1 = "account";
    private static final String TABLE2 = "transfer";

    // Table 1 columns
    private static final String ACCOUNT_NUMBER = "account_number";
    private static final String BANK = "bank";
    private static final String ACCOUNT_HOLDER = "account_holder";
    private static final String BALANCE = "balance";

    // Table 2 columns
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String AMOUNT = "amount";

    //private List<Account> accountList;
    //private List<String> accountNumbersList;

    public DbHandler(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE1_CREATE_QUERY = "CREATE TABLE " + TABLE1 + " " +
                "("
                + ACCOUNT_NUMBER + " TEXT PRIMARY KEY,"
                + BANK + " TEXT,"
                + ACCOUNT_HOLDER + " TEXT,"
                + BALANCE + " TEXT" +
                ");";

        String TABLE2_CREATE_QUERY = "CREATE TABLE " + TABLE2 + " " +
                "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ACCOUNT_NUMBER + " TEXT,"
                + DATE + " TEXT,"
                + TYPE + " TEXT,"
                + AMOUNT + " DOUBLE"+
                ");";

        db.execSQL(TABLE1_CREATE_QUERY);
        db.execSQL(TABLE2_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE1_QUERY = "DROP TABLE IF EXISTS " + TABLE1;
        String DROP_TABLE2_QUERY = "DROP TABLE IF EXISTS " + TABLE2;

        db.execSQL(DROP_TABLE1_QUERY);
        db.execSQL(DROP_TABLE2_QUERY);

        onCreate(db);
    }

    public void addAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NUMBER, account.getAccountNo());
        contentValues.put(BANK, account.getBankName());
        contentValues.put(ACCOUNT_HOLDER, account.getAccountHolderName());
        contentValues.put(BALANCE, account.getBalance());

        //save to table
        sqLiteDatabase.insert(TABLE1, null, contentValues);
        sqLiteDatabase.close();

    }

    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE1;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
                do{
                    // instantiate an account
                    Account account = new Account(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getDouble(3)
                    );
                    accountList.add(account);
                }
                while (cursor.moveToNext());
        }

        return accountList;
    }

    public List<String> getAccountNumberList(){
        List<String> accountNumbersList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + ACCOUNT_NUMBER + " FROM " + TABLE1;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
                do{
                    accountNumbersList.add(cursor.getString(0));
                }
                while (cursor.moveToNext());
        }

        return accountNumbersList;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * " + " FROM " + TABLE1
                + " WHERE " + ACCOUNT_NUMBER + "==?";

        String[] selectionArgs = {accountNo};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );

            return account;
        }
        cursor.close();
        db.close();

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE1, ACCOUNT_NUMBER+"=?", new String[]{accountNo});
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        Account account = getAccount(accountNo);

        contentValues.put(ACCOUNT_NUMBER, accountNo);
        contentValues.put(BANK, account.getBankName());
        contentValues.put(ACCOUNT_HOLDER, account.getAccountHolderName());

        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }

        String[] stringArgs = {accountNo};
        db.update(TABLE1, contentValues, ACCOUNT_NUMBER + "=?", stringArgs);

    }

    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount)  {
        SQLiteDatabase db = getWritableDatabase();
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues contentValues = new ContentValues();

        contentValues.put(ACCOUNT_NUMBER, accountNo);
        contentValues.put(DATE, sdf.format(date));
        contentValues.put(TYPE, expenseType.name());
        contentValues.put(AMOUNT, amount);

        db.insert(TABLE2, null, contentValues);
    }

    public List<Transaction> getAllTransactionLogs() throws ParseException {
        SQLiteDatabase db = getReadableDatabase();
        List<Transaction> transactionList = new ArrayList<>();
        ExpenseType expenseType;
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String query = "SELECT * FROM " + TABLE2;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(3).equals("EXPENSE")){
                    expenseType = ExpenseType.EXPENSE;
                }
                else{
                    expenseType = ExpenseType.INCOME;
                }
                date = sdf.parse(cursor.getString(2));

                Transaction transaction = new Transaction(
                    date,
                    cursor.getString(1),
                    expenseType,
                    cursor.getDouble(4)
                );

                transactionList.add(transaction);
            }while(cursor.moveToNext());
        }
        return transactionList;
    }
}
