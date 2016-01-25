package youmo.threadkiller;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Core.ProcessModel;
import Core.ThreadHelper;

/**
 * Created by tanch on 2016/1/13.
 */
public class ThrRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProcessModel> ProcessArray;
    private Context context;
    private static ItemClickListener listener;

    private final static int Type_Default=0;
    private final static int Type_NotTime=1;

    //接口,用于传递监听器
    public interface ItemClickListener {
        public void onItemClick(View view,int postion);
    }

    //开放对外方法，设置监听器
    public void SetItemClickListener(ItemClickListener listener)
    {
        this.listener=listener;
    }

    //初始化ThrRecAdapter
    public ThrRecAdapter(List<ProcessModel> data,Context _context) {
        ProcessArray = data;
        Collections.sort(data, new Comparator<ProcessModel>() {
            @Override
            public int compare(ProcessModel lhs, ProcessModel rhs) {
                if (lhs.lastTime<rhs.lastTime)
                    return 1;
                if (lhs.lastTime>rhs.lastTime)
                    return -1;
                return 0;
            }
        });
        this.context=_context;
    }

    //自定义Holder数据体，Holder->每一个Item的信息载体
    public static class ThrRecHolder extends RecyclerView.ViewHolder
    {
        private final TextView View_PackageName;
        private final TextView View_ProcessName;
        private final ImageView View_ThreadImage;

        CardView View_Card;
        public ThrRecHolder(View v) {
            super(v);
            View_PackageName=(TextView)v.findViewById(R.id.view_package_name);
            View_ProcessName=(TextView)v.findViewById(R.id.view_process_name);
            View_ThreadImage=(ImageView)v.findViewById(R.id.view_thread_image);
            View_Card=(CardView)v.findViewById(R.id.view_card);
            View_Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getAdapterPosition());
                }
            });
        }

        public TextView GetView_PackageName()
        {
            return View_PackageName;
        }
        public TextView GetView_ProcessName()
        {
            return View_ProcessName;
        }
        public ImageView GetView_ThreadImage()
        {
            return View_ThreadImage;
        }
    }

    public static class ThrTimeHolder extends RecyclerView.ViewHolder
    {
        private final TextView View_Time;
        private final TextView View_PackageName;
        private final TextView View_ProcessName;
        private final ImageView View_ThreadImage;

        CardView View_Card;
        public ThrTimeHolder(View v) {
            super(v);
            View_Time=(TextView)v.findViewById(R.id.view_thread_time);
            View_PackageName=(TextView)v.findViewById(R.id.view_package_name);
            View_ProcessName=(TextView)v.findViewById(R.id.view_process_name);
            View_ThreadImage=(ImageView)v.findViewById(R.id.view_thread_image);
            View_Card=(CardView)v.findViewById(R.id.view_card);
            View_Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getAdapterPosition());
                }
            });
        }
        public TextView GetView_Time()
        {
            return View_Time;
        }
        public TextView GetView_PackageName()
        {
            return View_PackageName;
        }
        public TextView GetView_ProcessName()
        {
            return View_ProcessName;
        }
        public ImageView GetView_ThreadImage()
        {
            return View_ThreadImage;
        }
    }
    //实例化Holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int TypeId) {
        View v=null;
        RecyclerView.ViewHolder vh=null;
        //根据 TypeId 匹配不同的 view
        switch (TypeId)
        {
            case 0:
                Log.i("信息","Type_Default");
                v=LayoutInflater.from(context).inflate(R.layout.recycler_item, viewGroup, false);
                vh=new ThrRecHolder(v);
                break;
            case 1:
                Log.i("信息","Type_NotTime");
                v=LayoutInflater.from(context).inflate(R.layout.thread_item_time, viewGroup, false);
                vh=new ThrTimeHolder(v);
                break;
            default:
                Log.i("信息","Type_Default");
                v=LayoutInflater.from(context).inflate(R.layout.recycler_item, viewGroup, false);
                vh = new ThrRecHolder(v);
                break;
        }
        return vh;
    }

    //数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ProcessModel p=ProcessArray.get(position);

        if (holder instanceof ThrRecHolder)
        {
            ((ThrRecHolder)holder).GetView_PackageName().setText(p.name);
            ((ThrRecHolder)holder).GetView_ProcessName().setText(p.processName);
            if (p.icon!=null)
                ((ThrRecHolder)holder).GetView_ThreadImage().setImageDrawable(p.icon);
        }
        else if (holder instanceof ThrTimeHolder )
        {
           // ((ThrTimeHolder)holder).GetView_Time().setText(new SimpleDateFormat("yyyyMMdd HH:MM:SS").format(new Date(p.lastTime)));
            ((ThrTimeHolder)holder).GetView_Time().setText(FriendlyDate(new Date(p.lastTime)));
            ((ThrTimeHolder)holder).GetView_PackageName().setText(p.name);
            ((ThrTimeHolder)holder).GetView_ProcessName().setText(p.processName);
            if (p.icon!=null)
                ((ThrTimeHolder)holder).GetView_ThreadImage().setImageDrawable(p.icon);
        }

    }
    //region 友好日期生成
    public static int daysOfTwo(Date originalDate, Date compareDateDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(originalDate);
        int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(compareDateDate);
        int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);
        return originalDay - compareDay;
    }
    public static String FriendlyDate(Date compareDate) {
        Date nowDate = new Date();
        int dayDiff = daysOfTwo(nowDate, compareDate);
        if (dayDiff <= 0)
            return "今日";
        else if (dayDiff == 1)
            return "昨日";
        else if (dayDiff == 2)
            return "前日";
        else
            return new SimpleDateFormat("M月d日 E").format(compareDate);
    }
    //endregion

    //    //返回 类型ID。
    @Override
    public int getItemViewType(int position)
    {
        //super.getItemViewType(position);
        if (position==0)
            return 1;
        Date p1Time=new Date(ProcessArray.get(position).lastTime);
        String p1= new SimpleDateFormat("yyyyMMdd 00:00:00").format(p1Time);
        Date p2Time=new Date(ProcessArray.get(position-1).lastTime);
        String p2= new SimpleDateFormat("yyyyMMdd 00:00:00").format(p2Time);
        return p1.equals(p2)? 0:1;
    }

    //取得数据大小
    @Override
    public int getItemCount() {
        return ProcessArray.size();
    }

    //region TODO:常用方法
    //常用方法，取得数据源，设置数据源,删除，添加，插入
    public List<ProcessModel> GetData()
    {
        return ProcessArray;
    }
    public void SetData(List<ProcessModel> Lpm)
    {
        this.ProcessArray=Lpm;
    }

    public void Remove(int postion)
    {
        ProcessArray.remove(postion);
        notifyItemRemoved(postion);
    }
    public void Add(ProcessModel objct)
    {
        ProcessArray.add(objct);
        notifyDataSetChanged();

    }
    public void Insert(ProcessModel objct,int postion)
    {
        ProcessArray.add(postion,objct);
        notifyItemInserted(postion);
    }
    //endregion


}
