package airfree.xprojets.airfree;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

public class Recherche extends ActionBarActivity {
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
            myAdapter = new SimpleCursorAdapter(this, R.layout.element, cursor, new String[]{"bookTitle",},new int[]{R.id.intitule});
            ArrayList results = new ArrayList();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String bookName = cursor.getString(cursor.getColumnIndex("bookTitle"));
                        bookTitles.add(bookName);
                    } while (cursor.moveToNext());
                }
            }
            myAdapter = new SimpleCursorAdapter(this, R.layout.element,  cursor, new String[]{"bookTitle"},new int[]{R.id.intitule});
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        //R.menu.menu est l'id de notre menu
        inflater.inflate(R.menu.menu, menu);
        return true;

    }
}
