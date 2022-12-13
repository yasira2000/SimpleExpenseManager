package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.DbHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private final DbHandler dbHandler;

    public PersistentMemoryAccountDAO(Context context) {
        this.dbHandler = new DbHandler(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return dbHandler.getAccountNumberList();
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHandler.getAccountsList();
    }

    // todo
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbHandler.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (dbHandler.getAccount(accountNo) != null){
            dbHandler.removeAccount(accountNo);
        }
        else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(dbHandler.getAccount(accountNo) != null){
            dbHandler.updateBalance(accountNo, expenseType, amount);
        }
        else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }
}
