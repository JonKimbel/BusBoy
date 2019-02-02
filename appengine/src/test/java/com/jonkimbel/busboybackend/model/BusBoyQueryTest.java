package com.jonkimbel.busboybackend.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

/**
 * Unit tests for {@link BusBoyQuery}.
 */
@RunWith(JUnit4.class)
public class BusBoyQueryTest {
  @Test
  public void getStopId() {
    BusBoyQuery stopIdBanana = new BusBoyQuery("?stop=banana");
    BusBoyQuery stopIdApple = new BusBoyQuery("?stop=apple");
    BusBoyQuery stopIdNull = new BusBoyQuery("?key=whatever");

    assertThat(stopIdBanana.getStopId()).isEqualTo("banana");
    assertThat(stopIdApple.getStopId()).isEqualTo("apple");
    assertThat(stopIdNull.getStopId()).isNull();
  }

  @Test
  public void isIncomplete() {
    BusBoyQuery completeQuery = new BusBoyQuery("?stop=whatever");
    BusBoyQuery incompleteQuery = new BusBoyQuery("?key=whatever");

    assertThat(completeQuery.isIncomplete()).isFalse();
    assertThat(incompleteQuery.isIncomplete()).isTrue();
  }

  @Test
  public void parseQueryString() throws Exception {
    BusBoyQuery result = new BusBoyQuery("key=a8pz=8&stop=ID");

    assertThat(result.queryMap).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_leadingQuestionMark() throws Exception {
    BusBoyQuery result = new BusBoyQuery("?key=a8pz=8&stop=ID");

    assertThat(result.queryMap).containsExactly("key", "a8pz=8", "stop","ID");
  }

  @Test
  public void parseQueryString_returnsEmptyForNullInput() throws Exception {
    BusBoyQuery result = new BusBoyQuery(null);

    assertThat(result.queryMap).isEmpty();
  }
}
