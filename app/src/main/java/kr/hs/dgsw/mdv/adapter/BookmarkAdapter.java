package kr.hs.dgsw.mdv.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.hs.dgsw.mdv.R;
import kr.hs.dgsw.mdv.activity.ReadPDFActivity;
import kr.hs.dgsw.mdv.activity.ReadTXTActivity;
import kr.hs.dgsw.mdv.database.DatabaseHelper;
import kr.hs.dgsw.mdv.item.BookmarkItem;
import kr.hs.dgsw.mdv.item.MainItem;

/**
 * Created by DH on 2018-05-14.
 */

public class BookmarkAdapter extends BaseAdapter{

    Context c;
    DatabaseHelper myDb;

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<BookmarkItem> listViewItemList = new ArrayList<BookmarkItem>() ;

    // ListViewAdapter의 생성자
    public BookmarkAdapter(Context c, ArrayList<BookmarkItem> listViewItemList) {
        this.c = c;
        this.listViewItemList = listViewItemList;
        myDb = new DatabaseHelper(c);
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_bookmark, parent, false);
        }

        final BookmarkItem item = (BookmarkItem)this.getItem(position);

        TextView itemName = (TextView) convertView.findViewById(R.id.bookmarkName);
        TextView itemPercent = (TextView) convertView.findViewById(R.id.bookmarkPercent);

        itemName.setText(item.getBookmarkName());
        itemPercent.setText(item.getBookmarkPercent());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadTXTActivity.readScroll.setScrollY(item.getBookmarkProcess());
                ReadTXTActivity.bookmarkDialog.dismiss();
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name, String path, int process, String percent) {
        BookmarkItem item = new BookmarkItem(name, path, process, percent);
        myDb.insertBookmarkData(name, path, process, percent);
        listViewItemList.add(item);
        notifyDataSetChanged();
    }
}
