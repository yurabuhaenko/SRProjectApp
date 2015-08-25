package denver.srprojectapp.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import denver.srprojectapp.R;
import denver.srprojectapp.objects.Project;

/**
 * Created by Denver on 15.07.2015.
 */
public class ProjectListAdapter  extends BaseAdapter {
    Context ctx;
    LayoutInflater layoutInflater;
    List<Project> projects;


    public ProjectListAdapter(Context context, List<Project> projects) {
        ctx = context;
        this.projects = projects;
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return projects.size();
    }


    @Override
    public Object getItem(int position) {
        return projects.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_project_list, parent, false);
        }

        Project project = projects.get(position);


        ((TextView) view.findViewById(R.id.text_view_title)).setText(project.getTitle());
        if(project.isEmptyDateTime() == false) {
            ((TextView) view.findViewById(R.id.text_view_date_time)).setText(project.getDatetime());
        }
        else {
            ((TextView) view.findViewById(R.id.text_view_date_time_text)).setText("");
            ((TextView) view.findViewById(R.id.text_view_date_time)).setText("");
        }
        ((TextView) view.findViewById(R.id.text_view_description)).setText(project.getDescription());
        ((TextView) view.findViewById(R.id.text_view_status)).setText(project.getStatusWord());



        return view;
    }




}

