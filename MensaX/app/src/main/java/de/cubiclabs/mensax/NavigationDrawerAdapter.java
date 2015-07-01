package de.cubiclabs.mensax;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.cubiclabs.mensax.models.Cafeteria;

/**
 * Created by thimokluser on 6/30/15.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<Cafeteria> {

    Context mContext;

    public NavigationDrawerAdapter(Context context, int resourceId, //resourceId=your layout
                                 List<Cafeteria> items) {
        super(context, resourceId, items);
        mContext = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView txtTitle;
        ImageView ivIcon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Cafeteria cafeteria = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.navdrawer_list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.text1);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(cafeteria.name);

        if(position < this.getCount() - 2) {
            holder.ivIcon.setVisibility(View.GONE);
        } else {
            holder.ivIcon.setVisibility(View.VISIBLE);
            if(position == this.getCount() - 2) {
                holder.ivIcon.setImageResource(R.drawable.rate_red);
            } else {
                holder.ivIcon.setImageResource(R.drawable.problem_red);
            }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount() + 2;
    }

    @Override
    public Cafeteria getItem(int position) {
        if(position == this.getCount() - 2) {
            Cafeteria rateMe = new Cafeteria();
            rateMe.name = mContext.getString(R.string.rate_app);
            return rateMe;
        }
        if(position == this.getCount() - 1) {
            Cafeteria reportIssue = new Cafeteria();
            reportIssue.name = mContext.getString(R.string.report_issue);
            return reportIssue;
        }

        return super.getItem(position);
    }
}
