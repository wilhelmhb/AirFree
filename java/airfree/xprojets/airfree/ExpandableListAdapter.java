package airfree.xprojets.airfree;

/**
 * Created by guillaume on 12/12/15.
 */
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String _listDataHeader[]; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private int _list_item_id;
    private int _item_title_id;
    private int _list_item_id2;
    private int _item_title_id2;
    private int _list_item_id3;
    private int _item_title_id3;
    private int _list_item_id4;
    private int _item_title_id4;
    private int _list_group_id;
    private int _group_title_id;


    public ExpandableListAdapter(Context context, String listDataHeader[],
                                 HashMap<String, List<String>> listChildData,
                                 int list_item_id, int list_item_id2, int list_item_id3, int list_item_id4, int list_group_id,
                                 int item_title_id, int item_title_id2, int item_title_id3, int item_title_id4, int group_title_id) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._list_item_id = list_item_id;
        this._item_title_id = item_title_id;
        this._list_item_id2 = list_item_id2;
        this._item_title_id2 = item_title_id2;
        this._list_item_id3 = list_item_id3;
        this._item_title_id3 = item_title_id3;
        this._list_item_id4 = list_item_id4;
        this._item_title_id4 = item_title_id4;
        this._list_group_id = list_group_id;
        this._group_title_id = group_title_id;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader[groupPosition]).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String incoming_text = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int childType = getChildType(groupPosition, childPosition);

        // We need to create a new "cell container"
        if (convertView == null || (Integer) convertView.getTag() != childType) {
            switch (childType) {
                case 0:
                    convertView = inflater.inflate(this._list_item_id, null);
                    convertView.findViewById(R.id.clic).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Recherche c = (Recherche) _context;
                            System.out.println("case appuyée");
                            if (((CheckBox) v).isChecked()) {
                                System.out.println("checked");
                                if (c.checked_vendors.size() == c.boutique.length) {
                                    c.checked_vendors = new LinkedList<String>();
                                    c.checked_vendors.add(((TextView) ((View) v.getParent()).findViewById(R.id.intitule)).getText().toString());
                                } else {
                                    c.checked_vendors.add(((TextView) ((View)v.getParent()).findViewById(R.id.intitule)).getText().toString());
                                }
                            } else {
                                System.out.println("unchecked");
                                c.checked_vendors.remove(((TextView) ((View)v.getParent()).findViewById(R.id.intitule)).getText().toString());
                            }
                        }
                    });
                    convertView.setTag(childType);
                    break;
                case 1:
                    convertView = inflater.inflate(this._list_item_id2, null);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Recherche c = (Recherche) _context;
                            c.prices[0] = Double.parseDouble(((RangeBar) ((View)v.getParent()).findViewById(R.id.rangebar_price)).getLeftPinValue());
                            c.prices[1] = Double.parseDouble(((RangeBar) ((View)v.getParent()).findViewById(R.id.rangebar_price)).getRightPinValue());
                            System.out.println("moved");
                        }
                    });
                    convertView.setTag(childType);
                    break;
                case 2:
                    convertView = inflater.inflate(this._list_item_id3, null);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Recherche c = (Recherche) _context;
                            c.notes[0] = Double.parseDouble(((RangeBar) ((View)v.getParent()).findViewById(R.id.rangebar_note)).getLeftPinValue());
                            c.notes[1] = Double.parseDouble(((RangeBar) ((View)v.getParent()).findViewById(R.id.rangebar_note)).getRightPinValue());
                            System.out.println("moved");
                        }
                    });
                    convertView.setTag(childType);
                    break;
                case 3:
                    convertView = inflater.inflate(this._list_item_id4, null);
                    convertView.findViewById(R.id.clic).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Recherche c = (Recherche) _context;
                            if (((CheckBox) v).isChecked()) {
                                System.out.println("checked");
                                c.available = 1;
                            } else {
                                System.out.println("unchecked");
                                c.available = 0;
                            }
                        }
                    });
                    convertView.setTag(childType);
                    break;
                default:
                    // Maybe we should implement a default behaviour but it should be ok we know there are 4 child types right?
                    break;
            }
        }
        // We'll reuse the existing one
        else {
            // There is nothing to do here really we just need to set the content of view which we do in both cases
        }

        switch (childType) {
            case 0:
                TextView description_child = (TextView) convertView.findViewById(this._item_title_id);
                description_child.setText(incoming_text);
                break;
            case 1:
                //Define how to render the data on the CHILD_TYPE_2 layout
                TextView description_child2 = (TextView) convertView.findViewById(this._item_title_id2);
                description_child2.setText(incoming_text);
                break;
            case 2:
                //Define how to render the data on the CHILD_TYPE_3 layout
                TextView description_child3 = (TextView) convertView.findViewById(this._item_title_id3);
                description_child3.setText(incoming_text);
                break;
            case 3:
            //Define how to render the data on the CHILD_TYPE_4 layout
                TextView description_child4 = (TextView) convertView.findViewById(this._item_title_id4);
                description_child4.setText(incoming_text);
                break;
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader[groupPosition])
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(_list_group_id, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(_group_title_id);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        //System.out.println(headerTitle);
        lblListHeader.setText(headerTitle);
        //System.out.println(lblListHeader.toString());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    @Override
    public int getChildTypeCount() {
        return 4; // I defined 5 child types (CHILD_TYPE_1, CHILD_TYPE_2, CHILD_TYPE_3, CHILD_TYPE_4, CHILD_TYPE_UNDEFINED)
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            default:
                return -1;
        }
    }
}