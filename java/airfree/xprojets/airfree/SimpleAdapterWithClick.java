package airfree.xprojets.airfree;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by guillaume on 12/12/15.
 */

class SimpleCursorAdapterWithClick extends SimpleCursorAdapter {
    public interface CallBack {
        void callback(CharSequence vendeur, CharSequence produit, CharSequence prix, float note, CharSequence description);
    }
    private CallBack callBack;
    private Context mContext;
    private Context appContext;
    private int layout_id;
    private Cursor cr;
    private final LayoutInflater inflater;
    private final int button_layout;
    private final String[] fields_names;


    public SimpleCursorAdapterWithClick(Context context,int layout_id, Cursor c,
                                        String[] fields_names, int[] fields_id, int button_layout, CallBack callBack) {
        super(context, layout_id, c, fields_names, fields_id);
        this.layout_id = layout_id;
        this.button_layout = button_layout;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.cr = c;
        this.callBack = callBack;
        this.fields_names = fields_names;
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout_id, parent, false);
        //bindView(v, context, cursor);
        //View v = new GridView(mContext);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //final int row_id = cursor.getColumnIndex("_id");  //Your row id (might need to replace)
        final TextView vendeur = (TextView) view.findViewById(R.id.vendeur);
        vendeur.setText(cursor.getString(cursor.getColumnIndex("boutique")));
        System.out.println(vendeur.getText());
        final TextView produit = (TextView) view.findViewById(R.id.intitule);
        produit.setText(cursor.getString(cursor.getColumnIndex("produit")));
        System.out.println(produit.getText());
        final TextView prix = (TextView) view.findViewById(R.id.prix);
        prix.setText(cursor.getString(cursor.getColumnIndex("prix")));
        System.out.println(prix.getText());
        final RatingBar note = (RatingBar) view.findViewById(R.id.notation);
        System.out.println(note);
        note.setRating(cursor.getFloat(cursor.getColumnIndex("note")));
        System.out.println(note.getRating());
        final TextView description = (TextView) view.findViewById(R.id.details);
        description.setText(cursor.getString(cursor.getColumnIndex("description")));
        System.out.println(description.getText());
        final Cursor c = cursor;
        ImageButton button = (ImageButton) view.findViewById(button_layout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD STUFF HERE you know which row is clicked. and which button
                System.out.println("button clicked : " + vendeur.getText());
                callBack.callback(vendeur.getText(), produit.getText(), prix.getText(),note.getRating(), description.getText());

            }
        });

    }

}