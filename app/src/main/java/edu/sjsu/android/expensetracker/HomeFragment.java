package edu.sjsu.android.expensetracker;




import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context mContext;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fab;
    private TodayListAdapter adapter;
    private ListView categoryListViewID;
    private TextView totalAmountID, totalItems, noExpense;
    private Button categoryButton;
    private ProgressBar progressBar;
    private Database db;
    private FragmentActivity mActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        onClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayList();
    }

    private void displayList() {

        class task extends AsyncTask<Void, Void, Void> {

            private long totalAmount;
            private List<String> categoryList;
            private int totalItemCount;

            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {

                db = new Database(mContext);
                totalAmount = db.getTodaysExpenseAmount();
                totalItemCount = (int) db.getTodaysExpensesCount();
                categoryList = db.getTodaysExpenseCategories();

                adapter = new TodayListAdapter(mContext, categoryList);

                return null;
            }

            @Override
            protected void onPostExecute(Void args) {

                displayPieChart();

                progressBar.setVisibility(View.INVISIBLE);

                totalAmountID.setText(Long.toString(totalAmount));

                if (totalItemCount == 0) {
                    noExpense.setVisibility(View.VISIBLE);
                } else {
                    noExpense.setVisibility(View.INVISIBLE);
                }
                totalItems.setText(getString(R.string.item) + "" + totalItemCount );
                categoryListViewID.setAdapter(adapter);
            }
        }
        new task().execute();
    }

    // Get the data for the PIE Chart from the DB
    private void displayPieChart() {

        ArrayList<String> categories = db.getTodaysExpenseCategories();

        ArrayList amtPer= new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            long amount = (db.getTodaysExpenseAmountByCategory(categories.get(i))*100)/db.getTodaysExpenseAmount();
            amtPer.add(new BarEntry(amount, i));
        }

        PieChart pieChart = mActivity.findViewById(R.id.pie_chart);
        PieDataSet pieDataSet = new PieDataSet(amtPer, getString(R.string.amount_text));
        pieDataSet.setValueFormatter(new PercentFormatter());
        pieChart.setTouchEnabled(false);
        pieChart.setCenterText(getString(R.string.expenses));
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setDescription(" ");
        pieChart.getLegend().setEnabled(false);
        PieData data = new PieData(categories, pieDataSet);
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieChart.setData(data);
        pieChart.animateXY(1000, 1000);
    }

    // Intializing the variables with the fields in XML file
    private void setIDs() {
        fab = getActivity().findViewById(R.id.newfab);
        categoryListViewID = getActivity().findViewById(R.id.today_list);
        totalAmountID = getActivity().findViewById(R.id.total_amount);
        totalItems = getActivity().findViewById(R.id.total_items);
        categoryButton = getActivity().findViewById(R.id.categories_button);
        noExpense = getActivity().findViewById(R.id.no_expense_textview);
        progressBar = getActivity().findViewById(R.id.progress_bar);

        TextView headerText = getActivity().findViewById(R.id.listview_header);
        headerText.setText(R.string.header);

        db = new Database(mContext);
    }

    // ActionListeners onClick
    private void onClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditExpense.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", -1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        categoryListViewID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String category = (String) categoryListViewID.getItemAtPosition(position);

                Intent expenseActivity = new Intent(view.getContext(), ExpenseListByCategory.class);
                expenseActivity.putExtra("category", category);
                // Send the intent to launch a new activity
                startActivity(expenseActivity);
            }
        });

        categoryListViewID.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
                ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationY", 10f);
                animation.setDuration(2000);
                animation.start();

            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryList = new Intent(getContext(), CategoryList.class);
                categoryList.putExtra("purpose", true);
                // Send the intent to launch a new activity
                startActivity(categoryList);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false);
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

