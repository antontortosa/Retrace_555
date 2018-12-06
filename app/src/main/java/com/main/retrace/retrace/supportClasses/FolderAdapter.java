package com.main.retrace.retrace.supportClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.retrace.retrace.EditFolderActivity;
import com.main.retrace.retrace.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Customized Folder Adapter.
 */
public class FolderAdapter extends Adapter<FolderAdapter.MyViewHolder> {
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
     * @param databaseManager reference. Only used to remove folders or tasks (inside TaskAdapter), folders of the database are not used.
     * @param folders         reference to the folders that will be displayed.
     */
    public FolderAdapter(DatabaseManager databaseManager, LinkedHashMap<String, Folder> folders, Context context) {
        this.mFolderData = folders;
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
        holder.mLayout.setBackgroundColor(Color.parseColor(folder.getColor()));
        holder.mTextViewTitle.setText(folder.getTitle());
        if(folder.getLocation().getPlace()!=null){
            holder.mTextPlace.setText(folder.getLocation().getPlace());
        }
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
                                databaseManager.getHome().getmFoldersVisibleIds().clear();
                                databaseManager.getHome().getmFoldersVisibleIds().add(folderId);
                                databaseManager.getHome().updateUI(false);
                                break;
                            case R.id.folder_menu_edit:
                                Intent intent = new Intent(context, EditFolderActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("FolderId", folderId);
                                intent.putExtra("FolderName", folder.getTitle());
                                intent.putExtra("Lat", folder.getLocation().getLatitude());
                                intent.putExtra("Long", folder.getLocation().getLongitude());
                                intent.putExtra("Place", folder.getLocation().getPlace());
                                intent.putExtra("Color", folder.getColor());
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
        if (mFolderData.get(folderId).getTasks() != null) {
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
     * @param mFolderData with the new folders.
     */
    public void setMFolderData(LinkedHashMap<String, Folder> mFolderData) {
        this.mFolderData = mFolderData;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    /**
     * Class that holds the views.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        /**
         * whole view
         */
        private RelativeLayout mLayout;
        /**
         * Title of the folder
         */
        private TextView mTextViewTitle;
        /**
         * Description of the place
         */
        private TextView mTextPlace;
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
         * Color of the folder.
         */
        private String folderColor;

        /**
         * Constructor.
         *
         * @param databaseManager reference.
         * @param itemView        of the current item.
         */
        private MyViewHolder(final DatabaseManager databaseManager, View itemView) {
            super(itemView);

            this.mLayout = itemView.findViewById(R.id.folder_item);
            this.mTextViewTitle = itemView.findViewById(R.id.folder_item_title);
            this.mTextPlace = itemView.findViewById(R.id.placeDescription);
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
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
                        ((TextInputEditText) view).setText("");
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
