package com.example.cleaningthecity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private ArrayList<Player> arrayList;
    private MyAdapter myAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        listView = (ListView)(findViewById(R.id.listView_records));
        progressBar = (ProgressBar) (findViewById(R.id.progressBar_result));
        new LoaderAsyncTask(this).execute(10);
        databaseHelper = new DatabaseHelper(this);
        arrayList= new ArrayList<>();
    }

    private void loadDataInListView() {
        // load data from SQLITE
        arrayList = databaseHelper.getAllData();
        myAdapter = new MyAdapter(this,arrayList);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    private static class LoaderAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<RecordsActivity> activityWeakReference;

        LoaderAsyncTask(RecordsActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            RecordsActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            // loading bar - do "heavy" job
            for (int i = 0; i <= integers[0]; i++) {
                publishProgress(i * 100 / integers[0]);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            RecordsActivity activity = activityWeakReference.get();
            return activity.getString(R.string.pb_Status);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            RecordsActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);
            RecordsActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, statusMsg, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
            activity.loadDataInListView();
        }
    }
}