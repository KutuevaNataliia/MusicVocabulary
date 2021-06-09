import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordInformationForm extends JFrame {

    GridBagLayout gridbag;

    JLabel wordLabel;
    JTextField wordField;
    JLabel mainLabel;
    JTextField mainField;
    JLabel translationLabel;
    JTextField translationField;
    JButton addForm;
    GridBagConstraints constraintsAdd;
    JButton save;
    GridBagConstraints constraintsSave;
    JLabel[] additionalLabels = new JLabel[5];
    JTextField[] additionalForms = new JTextField[5];
    GridBagConstraints[] constraintsAdditionalText = new GridBagConstraints[5];
    GridBagConstraints[] constraintsAdditionalLabel = new GridBagConstraints[5];

    int additionalFormsNumber = 0;
    WordInformation initialInformation;
    int initialKey;

    WordInformationForm(String wordSource) {

        gridbag = new GridBagLayout();
        getContentPane().setLayout(gridbag);

        wordLabel = new JLabel("Word ");
        GridBagConstraints constraintsWordLabel = new GridBagConstraints();
        constraintsWordLabel.fill = GridBagConstraints.EAST;
        constraintsWordLabel.anchor = GridBagConstraints.NORTHWEST;
        constraintsWordLabel.weightx = 1;
        constraintsWordLabel.weighty = 1;
        constraintsWordLabel.gridx = 0;
        constraintsWordLabel.gridy = 0;
        getContentPane().add(wordLabel, constraintsWordLabel);

        wordField = new JTextField();
        wordField.setText(wordSource);
        wordField.setEditable(false);
        GridBagConstraints constraintsWordField = new GridBagConstraints();
        constraintsWordField.fill = GridBagConstraints.HORIZONTAL;
        constraintsWordField.anchor = GridBagConstraints.NORTHWEST;
        constraintsWordField.weightx = 1;
        constraintsWordField.weighty = 1;
        constraintsWordField.gridx = 1;
        constraintsWordField.gridy = 0;
        getContentPane().add(wordField, constraintsWordField);

        mainLabel = new JLabel("Main Form ");
        GridBagConstraints constraintsMainLabel = new GridBagConstraints();
        constraintsMainLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsMainLabel.anchor = GridBagConstraints.NORTHWEST;
        constraintsMainLabel.weightx = 1;
        constraintsMainLabel.weighty = 1;
        constraintsMainLabel.gridx = 0;
        constraintsMainLabel.gridy = 1;
        getContentPane().add(mainLabel, constraintsMainLabel);

        mainField = new JTextField();
        GridBagConstraints constraintsMainField = new GridBagConstraints();
        constraintsMainField.fill = GridBagConstraints.HORIZONTAL;
        constraintsMainField.anchor = GridBagConstraints.NORTHWEST;
        constraintsMainField.weightx = 1;
        constraintsMainField.weighty = 1;
        constraintsMainField.gridx = 1;
        constraintsMainField.gridy = 1;
        getContentPane().add(mainField, constraintsMainField);

        translationLabel = new JLabel("Translation ");
        GridBagConstraints constraintsTranslationLabel = new GridBagConstraints();
        constraintsTranslationLabel.fill = GridBagConstraints.HORIZONTAL;
        constraintsTranslationLabel.anchor = GridBagConstraints.NORTHWEST;
        constraintsTranslationLabel.weightx = 1;
        constraintsTranslationLabel.weighty = 1;
        constraintsTranslationLabel.gridx = 0;
        constraintsTranslationLabel.gridy = 2;
        getContentPane().add(translationLabel, constraintsTranslationLabel);

        translationField = new JTextField();
        GridBagConstraints constraintsTranslationField = new GridBagConstraints();
        constraintsTranslationField.fill = GridBagConstraints.HORIZONTAL;
        constraintsTranslationField.anchor = GridBagConstraints.NORTHWEST;
        constraintsTranslationField.weightx = 1;
        constraintsTranslationField.weighty = 1;
        constraintsTranslationField.gridx = 1;
        constraintsTranslationField.gridy = 2;
        getContentPane().add(translationField, constraintsTranslationField);

        addForm = new JButton("Add another form");
        constraintsAdd = new GridBagConstraints();
        constraintsAdd.fill = GridBagConstraints.HORIZONTAL;
        constraintsAdd.anchor = GridBagConstraints.NORTHWEST;
        constraintsAdd.weightx = 1;
        constraintsAdd.weighty = 1;
        constraintsAdd.gridx = 0;
        constraintsAdd.gridy = 3;
        getContentPane().add(addForm, constraintsAdd);
        addForm.addActionListener(new AddAnotherFormListener());

        save = new JButton("Save");
        constraintsSave = new GridBagConstraints();
        constraintsSave.fill = GridBagConstraints.HORIZONTAL;
        constraintsSave.anchor = GridBagConstraints.NORTHWEST;
        constraintsSave.weightx = 1;
        constraintsSave.weighty = 1;
        constraintsSave.gridx = 0;
        constraintsSave.gridy = 4;
        getContentPane().add(save, constraintsSave);
        save.addActionListener(new SaveActionListener());

        findInformation(wordSource);
        if (initialInformation != null) {
            mainField.setText(initialInformation.mainForm);
            translationField.setText(initialInformation.translation);
            for(int i = 0; i < initialInformation.additionalFormsNumber; i++) {
                makeAdditionalForm(initialInformation.additionalForms[i]);
            }
        }

        setSize(640, 600);
    }

    private void closeForm() {
        this.dispose();
    }

    private class SaveActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (mainField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill the main form");
            } else if (translationField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Fill the translation form");
            } else {
                WordInformation newInformation = new WordInformation();
                newInformation.mainForm = mainField.getText();
                newInformation.translation = translationField.getText();
                newInformation.additionalForms = new String[5];
                for (int i = 0; i < additionalFormsNumber; i++) {
                    newInformation.additionalForms[i] = additionalForms[i].getText();
                }
                newInformation.additionalFormsNumber = additionalFormsNumber;
                DbConnection dbConnection = new DbConnection();
                dbConnection.open();
                if (initialKey == 0) {
                   dbConnection.addWordInformation(newInformation);
                   General.wordsWithInformation.add(newInformation);
                } else {
                    dbConnection.updateWordInformation(newInformation, initialKey);
                    General.wordsWithInformation.remove(initialInformation);
                    General.wordsWithInformation.add(newInformation);
                }
                dbConnection.close();

                closeForm();
            }
        }
    }

    private void findInformation(String word) {
        DbConnection dbConnection = new DbConnection();
        dbConnection.open();
        MyPair<WordInformation, Integer> infPair = dbConnection.getWordInformation(word);
        initialInformation = infPair.getFirst();
        initialKey = infPair.getSecond();
        dbConnection.close();
    }

    private void makeAdditionalForm(String text) {
        constraintsAdd.gridy = 4 + additionalFormsNumber;
        gridbag.setConstraints(addForm, constraintsAdd);
        constraintsSave.gridy = 5 + additionalFormsNumber;
        gridbag.setConstraints(save, constraintsSave);

        additionalLabels[additionalFormsNumber] = new JLabel("Additional Form " + (additionalFormsNumber + 1));
        constraintsAdditionalLabel[additionalFormsNumber] = new GridBagConstraints();
        constraintsAdditionalLabel[additionalFormsNumber] = new GridBagConstraints();
        constraintsAdditionalLabel[additionalFormsNumber].fill = GridBagConstraints.HORIZONTAL;
        constraintsAdditionalLabel[additionalFormsNumber].anchor = GridBagConstraints.NORTHWEST;
        constraintsAdditionalLabel[additionalFormsNumber].weightx = 1;
        constraintsAdditionalLabel[additionalFormsNumber].weighty = 1;
        constraintsAdditionalLabel[additionalFormsNumber].gridx = 0;
        constraintsAdditionalLabel[additionalFormsNumber].gridy = 3 + additionalFormsNumber;
        getContentPane().add(additionalLabels[additionalFormsNumber] , constraintsAdditionalLabel[additionalFormsNumber]);

        additionalForms[additionalFormsNumber] = new JTextField(text);
        constraintsAdditionalText[additionalFormsNumber] = new GridBagConstraints();
        constraintsAdditionalText[additionalFormsNumber].fill = GridBagConstraints.HORIZONTAL;
        constraintsAdditionalText[additionalFormsNumber].anchor = GridBagConstraints.NORTHWEST;
        constraintsAdditionalText[additionalFormsNumber].weightx = 1;
        constraintsAdditionalText[additionalFormsNumber].weighty = 1;
        constraintsAdditionalText[additionalFormsNumber].gridx = 1;
        constraintsAdditionalText[additionalFormsNumber].gridy = 3 + additionalFormsNumber;
        getContentPane().add(additionalForms[additionalFormsNumber], constraintsAdditionalText[additionalFormsNumber]);

        additionalFormsNumber++;

        getContentPane().validate();
        getContentPane().repaint();
    }

    private class AddAnotherFormListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (additionalFormsNumber == 0) {
                makeAdditionalForm("");
            } else if (additionalFormsNumber < 5){
                boolean empty = false;
                for (int i = 0; i < additionalFormsNumber; i++) {
                    if (additionalForms[i].getText().isEmpty()) {
                        empty = true;
                        break;
                    }
                }
                if (!empty) {
                    makeAdditionalForm("");
                } else {
                    JOptionPane.showMessageDialog(null, "Fill all previous additional forms");
                }
            }
        }
    }
}
