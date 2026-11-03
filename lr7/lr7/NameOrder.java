import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/** Порядок рядків за правилами української мови (є, і, ї, ґ стоять на своїх місцях). */
public final class NameOrder {
    public static final Comparator<String> UKRAINIAN =
            Collator.getInstance(Locale.of("uk"))::compare;

    private NameOrder() {}
}
