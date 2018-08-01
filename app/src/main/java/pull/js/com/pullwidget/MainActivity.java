package pull.js.com.pullwidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import widgets.PullLayout;

public class MainActivity extends AppCompatActivity {
    private PullLayout pullLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullLayout = findViewById(R.id.pullLayout);
        listView = findViewById(R.id.listView);
        ArrayList data = new ArrayList();
        for (int i = 0; i < 20; i++) {
            data.add("Data" + i);
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));
    }
}
