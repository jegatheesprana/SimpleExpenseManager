/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.R;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants.EXPENSE_MANAGER;
/**
 *
 */
public class AddAccountFragment extends Fragment implements View.OnClickListener {
    private ExpenseManager currentExpenseManager;
    private EditText accountNumber;
    private EditText bankName;
    private EditText accountHolderName;
    private EditText initialBalance;
    private Button addAccount;
    private List<Account> accounts;
    private TableLayout accountsTableLayout;
    private View rootView;

    public static AddAccountFragment newInstance(ExpenseManager expenseManager) {
        AddAccountFragment addAccountFragment = new AddAccountFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXPENSE_MANAGER, expenseManager);
        addAccountFragment.setArguments(args);
        return addAccountFragment;
    }

    public AddAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_account, container, false);
        accountNumber = (EditText) rootView.findViewById(R.id.account_num);
        bankName = (EditText) rootView.findViewById(R.id.bank_name);
        accountHolderName = (EditText) rootView.findViewById(R.id.account_holder_name);
        initialBalance = (EditText) rootView.findViewById(R.id.initial_balance);
        addAccount = (Button) rootView.findViewById(R.id.add_account);
        addAccount.setOnClickListener(this);

        currentExpenseManager = (ExpenseManager) getArguments().get(EXPENSE_MANAGER);
        accounts = new ArrayList<>();
        if (currentExpenseManager != null) {
            accounts = currentExpenseManager.getAccountList();
        }
        accountsTableLayout = (TableLayout) rootView.findViewById(R.id.accounts_table);
        TableRow accountsTableRowHeader = (TableRow) rootView.findViewById(R.id.accounts_table_header);

        generateTransactionsTable(rootView, accountsTableLayout,accounts);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_account:
                String accountNumStr = accountNumber.getText().toString();
                String bankNameStr = bankName.getText().toString();
                String accountHolderStr = accountHolderName.getText().toString();
                String initialBalanceStr = initialBalance.getText().toString();


                if (accountNumStr.isEmpty()) {
                    accountNumber.setError(getActivity().getString(R.string.err_acct_number_empty));
                    break;
                }

                if (bankNameStr.isEmpty()) {
                    bankName.setError(getActivity().getString(R.string.err_bank_name_empty));
                    break;
                }

                if (accountHolderStr.isEmpty()) {
                    accountHolderName.setError(getActivity().getString(R.string.err_acct_holder_empty));
                    break;
                }

                if (initialBalanceStr.isEmpty()) {
                    initialBalance.setError(getActivity().getString(R.string.err_init_balance_empty));
                    break;
                }

                if (currentExpenseManager != null) {
                    Account lastAccount = currentExpenseManager.addAccount(accountNumStr, bankNameStr, accountHolderStr,
                            Double.parseDouble(initialBalanceStr));
                    addRow(rootView, accountsTableLayout, lastAccount);
                }
                cleanUp();
                break;
        }
    }

    private void cleanUp() {
        accountNumber.getText().clear();
        bankName.getText().clear();
        accountHolderName.getText().clear();
        initialBalance.getText().clear();
    }

    private void generateTransactionsTable(View rootView, TableLayout accountsTableLayout,
                                           List<Account> accounts) {
        for (Account account : accounts) {
            addRow(rootView, accountsTableLayout, account);
        }
    }

    private void addRow (View rootView, final TableLayout accountsTableLayout,
                         final Account account) {
        final TableRow tr = new TableRow(rootView.getContext());
        TextView lDateVal = new TextView(rootView.getContext());

        TextView lAccountNoVal = new TextView(rootView.getContext());
        lAccountNoVal.setText(account.getAccountNo());
        tr.addView(lAccountNoVal);

        TextView accountHolder = new TextView(rootView.getContext());
        accountHolder.setText(account.getAccountHolderName().toString());
        tr.addView(accountHolder);

        TextView delBtn = new TextView(rootView.getContext());
        delBtn.setText("Delete");
        tr.addView(delBtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Delete",
                        Toast.LENGTH_SHORT).show();
                accountsTableLayout.removeView(tr);
                try {
                    currentExpenseManager.removeAccount(account.getAccountNo());
                } catch (InvalidAccountException e) {
                    e.printStackTrace();
                }
            }
        });
        accountsTableLayout.addView(tr);
    }
}
