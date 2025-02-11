package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

import IO.ScoreIO;
import model.ScoreBoardModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ScoreBoardView extends JFrame {
    private JScrollPane scroll;
    private DefaultTableModel tableModel;
    private JTable table;

    public ScoreBoardView() {
        super("User Score Board");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.BLACK);
        initModel(ScoreBoardModel.getColumnString());
        initTable();
        settingForTable(ScoreBoardModel.getColumnString());
        setFocusable(true);
        requestFocus();
        loadScoreBoard(ScoreBoardModel.getJsonArr());
        //각 칼럼을 누르면 칼럼별로 정렬됨
        TableRowSorter rowSorter = new TableRowSorter<TableModel>(table.getModel());
        rowSorter.addRowSorterListener(e -> updateRanks());
        table.setRowSorter(rowSorter);
    }

    public ScoreBoardView(String name, int score) {
        super("User Score Board");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(Color.BLACK);
        initModel(ScoreBoardModel.getColumnString());
        initTable();
        settingForTable(ScoreBoardModel.getColumnString());
        highlightScoreBoard(name, score);
        setFocusable(true);
        requestFocus();
        loadScoreBoard(ScoreBoardModel.getJsonArr());
        //각 칼럼을 누르면 칼럼별로 정렬됨
        TableRowSorter rowSorter = new TableRowSorter<TableModel>(table.getModel());
        rowSorter.addRowSorterListener(e -> updateRanks());
        table.setRowSorter(rowSorter);
    }

    private void initModel(String[] columnString) {
        tableModel = new DefaultTableModel();
        for (int i = 0; i < columnString.length; i++) {
            tableModel.addColumn(columnString[i]);
        }
    }

    private void initTable() {
        // 테이블 생성
        table = new JTable(tableModel);
        scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
        scroll.setBackground(Color.BLACK);
        CompoundBorder border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.CYAN, 10),
                BorderFactory.createLineBorder(Color.DARK_GRAY, 5));
        scroll.setBorder(border);
        //테이블을 수정불가능하게 설정
        table.setEnabled(false);
    }

    private void settingForTable(String[] columnString) {
        DefaultTableCellRenderer celAlignCenter = new DefaultTableCellRenderer();
        celAlignCenter.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columnString.length; i++) {
            table.getColumn(columnString[i]).setCellRenderer(celAlignCenter);
        }
        table.setRowHeight(50);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        Font newFont = new Font("Malgun Gothic", Font.PLAIN, 20);
        table.setFont(newFont);
        this.getContentPane().add(scroll, BorderLayout.CENTER);
    }

    private void loadScoreBoard(JSONArray jsonArr) {
        if (ScoreIO.isFileEmpty()) {
            JOptionPane.showMessageDialog(this, "저장된 스코어가 없습니다.", "Message", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            ScoreBoardModel.sortScore();
            // 일일이 꺼내서 JSONObject로 사용
            if (jsonArr.size() > 0) {
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject jsonObj = (JSONObject) jsonArr.get(i);
                    String name = (String) jsonObj.get("name");
                    Long score = (Long) jsonObj.get("score");
                    tableModel.addRow(new Object[]{i + 1, name, score});
                }
            }
        }
    }

    //게임이 끝난 후 스코어보드를 보여줄 때
    public void highlightScoreBoard(String name, int score) {
        // 이런식으로 강조하는 방법은 명세에 맞지 않아요
        JLabel nowUserScore = new JLabel();
        nowUserScore.setText(name + ": " + score);
        nowUserScore.setForeground(Color.ORANGE);
        nowUserScore.setFont(new Font("Malgun Gothic", Font.PLAIN, 20));
        nowUserScore.setBorder(new EmptyBorder(0, 0, 15, 0));
        this.getContentPane().add(nowUserScore, BorderLayout.NORTH);
        // 스코어 리스트 내에서 특정 row를 찾아서 그 row를 강조해야 함
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(name)
                    && tableModel.getValueAt(i, 2).equals(score)) {
                // 강조 로직 작성
            }
        }
    }

    //정렬 순서에 따른 순위 갱신 for Chatgpt 3.5
    private void updateRanks() {
        // Get the number of rows in the table model
        int rowCount = tableModel.getRowCount();

        // Update ranks based on sorted indices
        for (int i = 0; i < rowCount; i++) {
            int modelIndex = table.convertRowIndexToModel(i); // Convert view index to model index
            tableModel.setValueAt(i + 1, modelIndex, 0); // Set rank in the model
        }
    }
}
