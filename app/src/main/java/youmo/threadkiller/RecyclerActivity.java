package youmo.threadkiller;

import android.app.usage.UsageStatsManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import Core.ThreadHelper;

public class RecyclerActivity extends AppCompatActivity {

    ThrRecAdapter tra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView  recyclerView = (RecyclerView) findViewById(R.id.recyclerList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tra=new ThrRecAdapter(new ThreadHelper(this).getRunningProcess(UsageStatsManager.INTERVAL_DAILY),this);
        tra.SetItemClickListener(new ThrRecAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Toast.makeText(getApplicationContext(), tra.GetData().get(postion).name,Toast.LENGTH_LONG).show();
                tra.Remove(postion);
            }
        });
        // specify an adapter (see also next example)
        recyclerView.setAdapter(tra);
    }


}
