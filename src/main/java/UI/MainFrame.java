package UI;

import Entities.EfficiencyIndex;
import Entities.Player;
import Helpers.QueryHelper;
import UI.controller.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.Font;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class MainFrame {
    private EntityManager entityManager;
    private JButton goToPlayersButton;
    private JButton goToGoalsButton;
    private JButton goToMatchesButton;
    private JButton goToPositionsButton;
    private JButton goToEfficiencyIndexNames;
    private JButton goToEfficiencyIndexes;
    private JButton saveButton;
    private JButton addNewButton;
    private JButton deleteButton;
    private JButton saveToFileButton;
    private JButton loadFromFile;
    private JButton printReportButton;
    private JPanel workPanel;
    private JPanel content;

    private JToolBar topToolBar;
    private JToolBar menuToolBar;
    private JFrame frame;

    private TableProcessor currentTableProcessor;

    public MainFrame(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void show() {
        frame = new JFrame("Управление командой");
        frame.setSize(new Dimension(500, 300));
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        goToPlayersButton = new JButton("Игроки");
        goToGoalsButton = new JButton("Голы");
        goToMatchesButton = new JButton("Матчи");
        goToPositionsButton = new JButton("Позиции");
        goToEfficiencyIndexes = new JButton("Показатели эффективности");
        goToEfficiencyIndexNames = new JButton("<html><body style='text-align:center'>" +
                "Название<br>показателей<br>эффективности " +
                "</body></html>");
        saveButton = new JButton("Сохранить");
        deleteButton = new JButton("Удалить");
        addNewButton = new JButton("Добавить новое");
        saveToFileButton = new JButton("Сохранить в файл");
        loadFromFile = new JButton("Открыть файл");
        printReportButton = new JButton("Отчёт о показателях эффективности");

        goToPlayersButton.setToolTipText("К игрокам");
        goToGoalsButton.setToolTipText("К забитым мячам");
        goToMatchesButton.setToolTipText("К матчам");
        goToPositionsButton.setToolTipText("К позициям");
        goToEfficiencyIndexNames.setToolTipText("К названиям показателей эффективности");
        goToEfficiencyIndexes.setToolTipText("К показателям эффективности");
        saveButton.setToolTipText("Сохранить");
        addNewButton.setToolTipText("Добавить новое");
        deleteButton.setToolTipText("Удалить");
        saveToFileButton.setToolTipText("Сохранить в файл таблицу");
        loadFromFile.setToolTipText("Загрузить данные в таблицу из файла");
        printReportButton.setToolTipText("Отчёт");


        goToGoalsButton.addActionListener(e -> addNewContentPanel(new GoalsController(frame, entityManager)));
        goToPlayersButton.addActionListener(e -> addNewContentPanel(new PlayersController(frame, entityManager)));
        saveButton.addActionListener(e -> {if(currentTableProcessor != null) currentTableProcessor.saveTable();});
        deleteButton.addActionListener(deleteClickListener);
        goToPositionsButton.addActionListener(e -> addNewContentPanel(new PositionsController(frame, entityManager)));
        goToMatchesButton.addActionListener(e -> addNewContentPanel(new MatchesController(frame, entityManager)));
        goToEfficiencyIndexNames.addActionListener(e ->
                addNewContentPanel(new EfficiencyIndexNamesController(frame, entityManager)));
        goToEfficiencyIndexes.addActionListener(e ->
                addNewContentPanel(new EfficiencyIndexController(frame, entityManager)));
        printReportButton.addActionListener(e -> printReport());

        topToolBar = new JToolBar("Панель инструментов");
        topToolBar.add(saveButton);
        topToolBar.add(deleteButton);
        topToolBar.add(printReportButton);
        topToolBar.addSeparator(new Dimension(50, 0));
        topToolBar.setFloatable(false);

        menuToolBar = new JToolBar("Меню");
        menuToolBar.setLayout(new BoxLayout(menuToolBar, BoxLayout.Y_AXIS));
        menuToolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        goToPlayersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToPositionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToMatchesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToEfficiencyIndexNames.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToEfficiencyIndexes.setAlignmentX(Component.CENTER_ALIGNMENT);
        goToGoalsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuToolBar.add(goToPlayersButton);
        menuToolBar.add(goToMatchesButton);
        menuToolBar.add(goToPositionsButton);
        menuToolBar.add(goToEfficiencyIndexNames);
        menuToolBar.add(goToEfficiencyIndexes);
        menuToolBar.add(goToGoalsButton);
        menuToolBar.setSize(new Dimension(0, frame.getSize().height));
        menuToolBar.setFloatable(false);

        content = new JPanel();

        workPanel = new JPanel();
        workPanel.setLayout(new BorderLayout());
        workPanel.add(topToolBar, BorderLayout.NORTH);
        workPanel.add(content, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(menuToolBar, BorderLayout.WEST);
        frame.add(workPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private final ActionListener deleteClickListener = e -> {
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Вы уверены, что хотите удалить выбранные объекты?", "Удаление", JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION) {
            try {
                if(currentTableProcessor.isDeletionSafe()) currentTableProcessor.deleteObjects();
                else {
                    int notSafeDeletion = JOptionPane.showConfirmDialog(null,
                            "Удаление некоторых объектов приведёт к удалению связанных с ними объектов. Продолжить?", "Удаление", JOptionPane.YES_NO_OPTION);
                    if(notSafeDeletion == JOptionPane.YES_OPTION) {
                        currentTableProcessor.deleteObjects();
                    }
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    };

    private void addNewContentPanel(AbstractTableController controller) {
        currentTableProcessor = controller;
        workPanel.remove(content);
        content = currentTableProcessor.getPanel();
        workPanel.add(content, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void printReport() {
        Document document = new com.itextpdf.text.Document();
        try {
            BaseFont bf= BaseFont.createFont("./fonts/ArialMT.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            com.itextpdf.text.Font font=new com.itextpdf.text.Font(bf,10, Font.PLAIN);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.removeChoosableFileFilter(new CustomFileFilter("", "All Files"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new CustomFileFilter("pdf",".pdf"));

            int result = fileChooser.showSaveDialog(frame);
            if(result!=JFileChooser.APPROVE_OPTION) return;
            File chosenFile = fileChooser.getSelectedFile();
            String path = chosenFile.getAbsolutePath();
            if(!path.toLowerCase().endsWith(".pdf"))
                path += ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            PdfPTable table = new PdfPTable(2);
            Stream.of("Игрок", "Показатели эффективности").forEach(columnTitle -> {
                PdfPCell header = new PdfPCell();
                header.setPadding(5);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(columnTitle, font));
                header.setMinimumHeight(header.getCalculatedHeight()+20);
                header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(header);
            });
            List<Player> playerList = QueryHelper.get(entityManager).getPlayersList();
            for(int i = 0; i < playerList.size(); i++) {
                Player player = playerList.get(i);
                List<EfficiencyIndex> efficiencyIndexList =
                        QueryHelper.get(entityManager).getEfficiencyIndexesList("player_id='"+player.getId()+"'");
                table.addCell(new Phrase(player.getPlayerNameWithNumber(), font));
                if(efficiencyIndexList!=null) {
                    StringBuilder builder = new StringBuilder();
                    for (EfficiencyIndex index :
                            efficiencyIndexList) {
                        builder.append(index.toString()).append("\n");
                    }
                    table.addCell(new Phrase(builder.toString(), font));
                } else {
                    table.addCell("");
                }
            }
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static class CustomFileFilter extends FileFilter {
        private final String description;
        private final String extension;

        public CustomFileFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            return (f.getName().toLowerCase().endsWith(extension));
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
