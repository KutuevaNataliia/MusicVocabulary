import java.util.Arrays;
import java.util.Objects;

public class WordInformation {
    public String mainForm;
    public String translation;
    public String[] additionalForms = new String[5];
    public int additionalFormsNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordInformation that = (WordInformation) o;
        return additionalFormsNumber == that.additionalFormsNumber && mainForm.equals(that.mainForm) && translation.equals(that.translation) && Arrays.equals(additionalForms, that.additionalForms);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mainForm, translation, additionalFormsNumber);
        result = 31 * result + Arrays.hashCode(additionalForms);
        return result;
    }
}
