import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


public class GUI extends JFrame{
    boolean isChecked = false;
    Path path;
    DBConnection connection;

    private DefaultTableModel tableModel;
    private JTable table;

    //Constructor
    public GUI() throws HeadlessException {
        try {
            connection = new DBConnection();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        setTitle("MSSQL");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(580, 350));
        JRadioButton includeTop = new JRadioButton("Include top");
        JTextArea topLimit = new JTextArea(1,4);
        topLimit.setEnabled(false);
        topLimit.setText(null);
        JButton loadButton = new JButton("Select");
        JComboBox<String> tableSelector = new JComboBox<>();
        JButton toTxtButton = new JButton("To text file");

        //Filling combobox
        try {
            ResultSet resultSet = connection.getConnection().getMetaData().getTables(null,null,"%", new String[]{"TABLE"});
            while (resultSet.next()){
                tableSelector.addItem(resultSet.getString(2)+"."+resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //Action Listeners
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableSelector.getSelectedItem() != null){
                    fetchData(tableSelector.getSelectedItem().toString(), isChecked, topLimit.getText());
                }else {
                    tableModel.addRow(new String[]{"Enter a valid table name"});
                }
            }
        });

        includeTop.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isChecked = !isChecked;
                topLimit.setEnabled(!topLimit.isEnabled());
            }
        });

        toTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeToFile(tableSelector.getSelectedItem().toString());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(tableSelector);
        buttonPanel.add(toTxtButton);
        buttonPanel.add(includeTop);
        buttonPanel.add(topLimit);
        getContentPane().setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchData(String tablename, boolean includeTop, String limit){
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        try{
            ResultSet resultSet = connection.selectAllFromTable(tablename, includeTop, limit);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++){
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            while (resultSet.next()){
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++){
                    row[i] = resultSet.getObject(i+1);
                }
                tableModel.addRow(row);
            }
            resultSet.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void writeToFile(String filename){
        path = Paths.get(filename+".txt");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableModel.getRowCount(); i++){
            for (int j = 0; j < tableModel.getColumnCount(); j++){
                if(tableModel.getValueAt(i,j) != null){
                    sb.append(tableModel.getValueAt(i,j));
                }
                else {
                    sb.append("\7");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        try {
            Files.write(path, sb.toString().getBytes());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
