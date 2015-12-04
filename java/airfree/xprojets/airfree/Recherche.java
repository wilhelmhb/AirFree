package airfree.xprojets.airfree;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import android.widget.GridView;
import android.view.Menu;
import android.view.MenuInflater;

public class Recherche extends Activity {
    private List bookTitles;
    private final String dbName = "Android";
    private static SQLiteDatabase sqliteDB = null;
    private SimpleCursorAdapter myAdapter;
    private final String tableName = "BestSellers";
    private final String[] bookTitle = new String[] {
            "The Great Gatsby",
            "The Grapes of Wrath",
            "Invisible Man",
            "Gone with the Wind",
            "Pride and Prejudice",
            "Wuthering Heights",
            "The Color Purple",
            "Midnights Children",
            "Mrs Dalloway",
            "War and Peace" };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        sqliteDB = null;
        try {
            sqliteDB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
            sqliteDB.execSQL("DROP TABLE " + tableName +";");
            sqliteDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY, bookTitle VARCHAR);");
            int i = 0;
            for (String ver : bookTitle) {
                sqliteDB.execSQL("INSERT INTO " + tableName + " Values ("+i+",'" + ver + "');");
                i++;
            }
            Cursor cursor = sqliteDB.rawQuery("SELECT id as _id, bookTitle FROM " + tableName, null);
            bookTitles = new ArrayList();
            myAdapter = new SimpleCursorAdapter(this, R.layout.article, cursor, new String[]{"bookTitle",},new int[]{R.id.intitule});
            ArrayList results = new ArrayList();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String bookName = cursor.getString(cursor.getColumnIndex("bookTitle"));
                        bookTitles.add(bookName);
                    } while (cursor.moveToNext());
                }
            }
            myAdapter = new SimpleCursorAdapter(this, R.layout.article,  cursor, new String[]{"bookTitle"},new int[]{R.id.intitule});
            GridView view = (GridView) findViewById(R.id.grille);
            view.setAdapter(myAdapter);
        }
        catch(SQLiteException e) {System.out.println("pas cool");}
        finally {
            if (sqliteDB != null) {
                sqliteDB.execSQL("DELETE FROM " + tableName);
                sqliteDB.close();
            }
        }
    }
}
