package airfree.xprojets.airfree;

/**
 * Created by guillaume on 12/12/15.
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private String _listDataHeader[]; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private int _list_item_id;
    private int _list_group_id;
    private int _item_title_id;
    private int _group_title_id;


    public ExpandableListAdapter(Context context, String listDataHeader[],
                                 HashMap<String, List<String>> listChildData,
                                 int list_item_id, int list_group_id, 
                                 int item_title_id, int group_title_id) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._list_item_id = list_item_id;
        this._list_group_id = list_group_id;
        this._item_title_id = item_title_id;
        this._group_title_id = group_title_id;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader[groupPosition])
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(_list_item_id, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(_item_title_id);

        txtListChild.setText(childText);
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
        System.out.println(headerTitle);
        lblListHeader.setText(headerTitle);
        System.out.println(lblListHeader.toString());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
