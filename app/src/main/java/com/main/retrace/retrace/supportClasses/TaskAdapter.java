package com.main.retrace.retrace.supportClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.main.retrace.retrace.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is the TaskAdapter.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    /**
     * Reference to the DatabaseManager.
     */
    private DatabaseManager databaseManager;

    /**
     * Id of the folder where the tasks are.
     */
    private String folderId;

    /**
     * Tasks.
     */
    private HashMap<String, Task> tasks;

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
     * @param databaseManager the data.
     * @param context         context of the app.
     */
    public TaskAdapter(DatabaseManager databaseManager, String folderId, Context context) {
        super(context, R.layout.task_item, new ArrayList<Task>(databaseManager.getFolders().get(folderId).getTasks().values()));
        this.tasks = new LinkedHashMap<String, Task>(databaseManager.getFolders().get(folderId).getTasks());
        this.databaseManager = databaseManager;
        this.folderId = folderId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        final String taskId = new ArrayList<String>(tasks.keySet()).get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        MyTaskViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new MyTaskViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            viewHolder.mCheckBox = convertView.findViewById(R.id.task_item_checkBox);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyTaskViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.mCheckBox.setText(task.getName());

        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                databaseManager.removeTask(folderId, taskId);
                                                            }
                                                        }
        );

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
        private CheckBox mCheckBox;
    }
}
