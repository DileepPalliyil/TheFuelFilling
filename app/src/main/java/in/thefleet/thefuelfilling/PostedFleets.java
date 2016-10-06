package in.thefleet.thefuelfilling;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PostedFleets extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter postedAdapter;
    ListView pListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_fleets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pListView = (ListView) findViewById(R.id.postedList);

        postedAdapter = new SimpleCursorAdapter(this,
                R.layout.item_posted,
                null,
                new String[] { PostedDBOpenHelper.SFLEETS_REGNO,PostedDBOpenHelper.SFLEETS_OKM,PostedDBOpenHelper.SFLEETS_CKM,
                        PostedDBOpenHelper.SFLEETS_QTY,PostedDBOpenHelper.SFLEETS_CREATED},
                new int[] { R.id.textPRegno,R.id.textPOKM,R.id.textPCKM,R.id.textPQTY,R.id.textSAVED }, 0);
       // if (postedAdapter.getCount()>0) {
            pListView.setAdapter(postedAdapter);
       // }


        getLoaderManager().initLoader(0, null, this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,PostedDataSource.CONTENT_URI3,
                null,null,null,null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        postedAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        postedAdapter.swapCursor(null);
    }
}
