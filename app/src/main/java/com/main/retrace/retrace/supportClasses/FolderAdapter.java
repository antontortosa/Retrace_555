package com.main.retrace.retrace.supportClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.main.retrace.retrace.R;

import java.util.ArrayList;

/**
 * Customized Folder Adapter.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    /**
     * Folder Data.
     */
    private ArrayList<Folder> mFolderData;

    /**
     * Reference to the context.
     */
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)

    /**
     * Constructor for the class.
     *
     * @param folders the data.
     */
    public FolderAdapter(ArrayList<Folder> folders, Context context) {
        this.mFolderData = folders;
        this.context = context;
    }

    /**
     * This method creates new views (invoked by the layout manager).
     */
    @Override
    public FolderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the custom layout
        View folderView = inflater.inflate(R.layout.folder_item, parent, false);

        // Return a new holder instance
        MyViewHolder viewHolder = new MyViewHolder(folderView);

        return viewHolder;
    }

    /**
     * This method replaces the contents of a view (invoked by the layout manager).
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // Get the data model based on position
        Folder folder = mFolderData.get(position);

        // - replace the contents of the view with that element
        holder.mTextViewTitle.setText(mFolderData.get(position).getTitle());
        // Same for tasks.

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.folder_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.folder_menu_open:
                                //handle menu1 click
                                break;
                            case R.id.folder_menu_edit:
                                //handle menu2 click
                                break;
                            case R.id.folder_menu_delete:
                                //handle menu3 click
                                break;
                        }
                        return false;
                    }
                });
                // Displaying the popup
                popup.show();

            }
        });

        TaskAdapter taskAdapter = new TaskAdapter(mFolderData.get(position).getTasks(), context);

        holder.tasks.setAdapter(taskAdapter);

        // To get the right height of the ListView.
        int totalHeight = 0;
        for (int i = 0; i < holder.tasks.getCount(); i++) {
            View listItem = taskAdapter.getView(i, null, holder.tasks);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = holder.tasks.getLayoutParams();
        params.height = totalHeight + (holder.tasks.getDividerHeight() * (taskAdapter.getCount() - 1));
        holder.tasks.setLayoutParams(params);
        holder.tasks.requestLayout();

        holder.tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO
            }
        });
    }

    /**
     * Returns the size of your dataset (invoked by the layout manager).
     */
    @Override
    public int getItemCount() {
        return mFolderData.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    /**
     * Class that holds the views.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        /**
         * Title of the folder
         */
        public TextView mTextViewTitle;
        /**
         * RecyclerView with the tasks.
         */
        public ListView tasks;
        /**
         * Folder options.
         */
        public TextView buttonViewOption;

        /**
         * Constructor.
         *
         * @param itemView
         */
        public MyViewHolder(View itemView) {
            super(itemView);

            this.mTextViewTitle = (TextView) itemView.findViewById(R.id.item_folder_title);
            this.tasks = (ListView) itemView.findViewById(R.id.item_folder_tasks);
            this.buttonViewOption = (TextView) itemView.findViewById(R.id.item_folder_menu);
        }
    }
}