package com.main.retrace.retrace.supportClasses;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.main.retrace.retrace.EditFolderActivity;
import com.main.retrace.retrace.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Customized Folder Adapter.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    /**
     * Folder Data.
     */
    private LinkedHashMap<String, Folder> mFolderData;

    /**
     * Reference to the context.
     */
    private Context context;

    /**
     * Reference to the Database Manager.
     */
    private DatabaseManager databaseManager;

    /**
     * Constructor for the class.
     *
     * @param databaseManager reference.
     */
    public FolderAdapter(DatabaseManager databaseManager, Context context) {
        this.mFolderData = new LinkedHashMap<String, Folder>(databaseManager.getFolders());
        this.context = context;
        this.databaseManager = databaseManager;
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
        return new MyViewHolder(databaseManager, folderView);
    }

    /**
     * This method replaces the contents of a view (invoked by the layout manager).
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // Get the data model based on position
        Folder folder = new ArrayList<Folder>(mFolderData.values()).get(position);
        String folderId = new ArrayList<String>(mFolderData.keySet()).get(position);

        // - replace the contents of the view with that element
        holder.mTextViewTitle.setText(folder.getTitle());
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
                                //TODO: folder menu open create.
                                break;
                            case R.id.folder_menu_edit:
                                Intent intent = new Intent(context, EditFolderActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("FolderId", folderId);
                                intent.putExtra("FolderName", folder.getTitle());
                                intent.putExtra("Lat", folder.getLocation().getLatitude());
                                intent.putExtra("Long", folder.getLocation().getLongitude());
                                context.startActivity(intent);
                                break;
                            case R.id.folder_menu_delete:
                                //handle menu3 click
                                databaseManager.removeFolder(folderId);
                                break;
                        }
                        return false;
                    }
                });
                // Displaying the popup
                popup.show();

            }
        });

        holder.setFolderId(folderId);

        // Before creating the TaskAdapter let's check if there are any tasks.
        if (databaseManager.getFolders().get(folderId).getTasks() != null) {
            TaskAdapter taskAdapter = new TaskAdapter(databaseManager, folderId, context);

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
    }

    /**
     * Returns the size of your dataset (invoked by the layout manager).
     */
    @Override
    public int getItemCount() {
        return mFolderData.size();
    }

    /**
     * Setter for the folders.
     *
     * @param mFolderData
     */
    public void setmFolderData(LinkedHashMap<String, Folder> mFolderData) {
        this.mFolderData = mFolderData;
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
        private TextView mTextViewTitle;
        /**
         * RecyclerView with the tasks.
         */
        private ListView tasks;
        /**
         * Folder options.
         */
        private TextView buttonViewOption;
        /**
         * Reference to the TectInputEditText textedit for the creation of new tasks.
         */
        private TextInputEditText newTaskTextEdit;
        /**
         * Reference to the folderId where the TextEdit is located.
         */
        private String folderId;

        /**
         * Constructor.
         *
         * @param databaseManager reference.
         * @param itemView        of the current item.
         */
        private MyViewHolder(final DatabaseManager databaseManager, View itemView) {
            super(itemView);

            this.mTextViewTitle = itemView.findViewById(R.id.folder_item_title);
            this.tasks = itemView.findViewById(R.id.folder_item_tasks);
            this.buttonViewOption = itemView.findViewById(R.id.folder_item_menu);
            this.newTaskTextEdit = itemView.findViewById(R.id.folder_item_textinputlayout_edittext);

            // New task listener
            newTaskTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String text = v.getText().toString();
                        databaseManager.writeTask(folderId, new Task(text, Calendar.getInstance().getTime(), null));
                        return true;
                    }
                    return false;
                }
            });

            // To make sure enters are captured.
            newTaskTextEdit.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                    if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String text = ((TextView) view).getText().toString();
                        databaseManager.writeTask(folderId, new Task(text, Calendar.getInstance().getTime(), null));
                        return true;
                    }
                    return false;
                }
            });
        }

        /**
         * Setter for the folderId.
         *
         * @param folderId folderId where the textedit is located.
         */
        public void setFolderId(String folderId) {
            this.folderId = folderId;
        }
    }
}
