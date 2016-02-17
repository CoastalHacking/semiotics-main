package io.opensemantics.semiotics.search.eclipse.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.SearchMatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.opensemantics.semiotics.search.SearchResult;
import io.opensemantics.semiotics.search.eclipse.EclipseFactory;
import io.opensemantics.semiotics.search.eclipse.EclipseJavaSearch;
import io.opensemantics.semiotics.search.mock.MockFactory;

public class SearchRequestorAdapterTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void shouldAcceptAndAdapt() throws Exception {
    final String description = "This is a description";
    EclipseJavaSearch search = EclipseFactory.eINSTANCE.createEclipseJavaSearch();
    search.setDescription(description);

    SearchResult result = MockFactory.eINSTANCE.createMockSearchResult();

    IJavaElement javaElement = Mockito.mock(IJavaElement.class);
    SearchMatch match = new SearchMatch(javaElement, SearchMatch.A_ACCURATE, 5, 10, null, null);

    SearchRequestorAdapter adapter = new SearchRequestorAdapter(search, result);
    adapter.acceptSearchMatch(match);

    assertTrue(result.getMatches().size() == 1);
    assertNotNull(result.getDescription());
    io.opensemantics.semiotics.search.SearchMatch searchMatch = result.getMatches().get(0);
    assertNotNull(searchMatch.getDescription());
    assertTrue(searchMatch.getDescription().equals(result.getDescription()));
  }

}
