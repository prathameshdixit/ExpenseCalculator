package edu.sjsu.android.expensetracker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private HistoryListAdapter historyListExpenseAdapter;
    private ListView expenseListView;
    private ProgressBar progressBar;
    private TextView noExpense;
    private Context mContext;
    private FragmentActivity mActivity;
    private Database db;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setIDs();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayList();
    }

    private void displayList() {

        // Creating an asynchronous task to get the expenses from the DB
        class task extends AsyncTask<Void, Void, Void> {

            List<ExpenseClass> expenseList;

            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                db = new Database(mContext);

                expenseList = db.getAllExpenses();
                return null;
            }

            @Override
            protected void onPostExecute(Void args) {

                progressBar.setVisibility(View.INVISIBLE);

                historyListExpenseAdapter = new HistoryListAdapter(mContext, expenseList);

                if (expenseList.size()== 0) {
                    noExpense.setVisibility(View.VISIBLE);
                } else {
                    noExpense.setVisibility(View.INVISIBLE);
                }
                expenseListView.setAdapter(historyListExpenseAdapter);
            }
        }
        new task().execute();

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ExpenseClass expense = historyListExpenseAdapter.getItem(position);

                Intent expenseActivity = new Intent(view.getContext(), ShowExpense.class);
                expenseActivity.putExtra("id", expense.getmID());
                // Send the intent to launch a new activity
                startActivity(expenseActivity);
            }
        });
    }

    private void setIDs() {
        noExpense = getActivity().findViewById(R.id.no_expense_textview);
        expenseListView = getActivity().findViewById(R.id.expense_list);
        progressBar = getActivity().findViewById(R.id.progress_bar);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.history_fragment, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mContext = context;
            mActivity = getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
