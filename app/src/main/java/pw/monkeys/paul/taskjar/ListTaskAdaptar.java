package pw.monkeys.paul.taskjar;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * Created by Paul on 8/14/2014.
 */
public class ListTaskAdaptar extends ArrayAdapter<TaskItem> {

    Context context;

    public ListTaskAdaptar(Context context, int resourceId,
                                 List<TaskItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView nameText;
        TextView hoursText;
        TextView creatorText;
        TextView assignedText;
        TextView hoursComplete;
        ProgressBar progressBar;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        TaskItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.taskrow, null);
            holder = new ViewHolder();
            holder.nameText = (TextView) convertView.findViewById(R.id.nameText);
            holder.hoursText = (TextView) convertView.findViewById(R.id.hoursText);
            holder.creatorText = (TextView) convertView.findViewById(R.id.creatorText);
            holder.assignedText = (TextView) convertView.findViewById(R.id.assignedText);
            holder.hoursComplete = (TextView) convertView.findViewById(R.id.hoursComplete);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.hoursText.setText(rowItem.getHours()+" Hr  ");
        holder.nameText.setText(rowItem.getName());
        holder.hoursComplete.setText(rowItem.getHoursComplete()+" Hrs complete  ");
        holder.creatorText.setText("Created by: "+rowItem.getCreator());
        holder.assignedText.setText("Assigned to: "+rowItem.getAssigned());
        holder.progressBar.setMax(Double.valueOf(rowItem.getHours()).intValue());
        holder.progressBar.setProgress(Double.valueOf(rowItem.getHoursComplete()).intValue());

        if(rowItem.getAssigned() == "null") {
            holder.assignedText.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}