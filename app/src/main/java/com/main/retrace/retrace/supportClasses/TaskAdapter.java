package com.main.retrace.retrace.supportClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.main.retrace.retrace.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class is the TaskAdapter.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    /**
     * Task Data.
     */
    private LinkedHashMap<String, Task> mTaskData;

    /**
     * Reference to the context.
     */
    private Context context;

    /**
     * Stores the last position.
     */
    private int lastPosition = -1;

    /**
     * Constructor for the class.
     *
     * @param tasks the data.
     */
    public TaskAdapter(LinkedHashMap<String, Task> tasks, Context context) {
        super(context, R.layout.task_item, new ArrayList<Task>(tasks.values()));
        this.mTaskData = tasks;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        MyTaskViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new MyTaskViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.task_item_checkBox);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyTaskViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.mCheckBox.setText(task.getName());
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Class that holds the views.
     */
    public static class MyTaskViewHolder {
        /**
         * Checkbox.
         */
        public CheckBox mCheckBox;
    }
}
