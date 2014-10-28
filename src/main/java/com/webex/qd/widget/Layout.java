package com.webex.qd.widget;

import net.sf.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: justin
 * Date: 7/8/13
 * Time: 2:54 PM
 */
public class Layout {
    private List<Column> columns = new LinkedList<Column>();

    public void addColumn(Column col) {
        columns.add(col);
        col.setId(columns.size());
    }

    public Column getColumn(int index) {
        return columns.get(index);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean isEmpty() {
        for (Column column : columns) {
            if (!column.isEmpty())
                return false;
        }
        return true;
    }

    public static Layout fromJson(String json) {
        Layout layout = new Layout();
        try {
            JSONArray layoutJson = JSONArray.fromObject(json);
            for (Object o : layoutJson) {
                if (!(o instanceof JSONArray)) {
                    continue;
                }

                JSONArray columnJson = (JSONArray) o;
                layout.addColumn(toColumn(columnJson));
            }
        } catch (Exception e) {

        }
        return layout;
    }

    private static Column toColumn(JSONArray json) {
        Column col = new Column();
        for (Object o : json) {
            if (o instanceof String) {
                try {
                    col.addWidget(Integer.valueOf((String) o));
                } catch (NumberFormatException ignore) {}
            }
        }
        return col;
    }

    public static class Column {
        private int id = 0;
        private List<Integer> widgets = new LinkedList<Integer>();

        public void addWidget(int widgetId) {
            widgets.add(Integer.valueOf(widgetId));
        }

        public List<Integer> getWidgetIds() {
            return widgets;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isEmpty() {
            return widgets.isEmpty();
        }
    }
}
