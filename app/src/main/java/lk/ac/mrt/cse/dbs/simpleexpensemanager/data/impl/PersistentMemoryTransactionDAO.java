package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private final DbHandler dbHandler;
    private List<Transaction>transactions;

    //todo
    public PersistentMemoryTransactionDAO(Context context) {
        this.dbHandler = new DbHandler(context);
        transactions = new ArrayList<>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
//        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
//        transactions.add(transaction);

        dbHandler.logTransaction(date, accountNo, expenseType, amount);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        try {
            System.out.println("ssss");
            //transactions = null;
            transactions = dbHandler.getAllTransactionLogs();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        try {
            transactions = dbHandler.getAllTransactionLogs();
        }catch(Exception e){
            System.out.println(e);
        }
        int size = transactions.size();
        System.out.println(size);
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
