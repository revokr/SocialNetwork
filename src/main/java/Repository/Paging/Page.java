package Repository.Paging;

public class Page<E> {
    private Iterable<E>elementsOnPage;
    private int totalNrOfElements;

    public Page(Iterable<E> elementsOnPage, int totalNrOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNrOfElements = totalNrOfElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNrOfElements() {
        return totalNrOfElements;
    }
}
