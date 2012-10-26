import com.example.dev.GroovyBitly
import com.example.dev.BitlyException
import groovy.util.GroovyTestCase

class GroovyBitlyTestCase extends GroovyTestCase {
  def username = System.getenv()['BITLY_USER']
  def apikey = System.getenv()['BITLY_APIKEY']
  def url = "http://www.google.com/"
  def g

  void setUp() {
    g = new GroovyBitly(username, apikey)
  }

  void testPreConditions() {
    assert g.username == username
    assert g.apikey == apikey
  }
  
  void testShorten() {
    def result = g.shorten(url)
    
    assert result.long_url == url
    assert result.hash != null
    assert result.global_hash != null
  }

  void testInfo() {
    def shortened = g.shorten(url)
    def result = g.info(shortened.url)
    
    assert shortened.url == result.short_url
    assert result.user_hash != null
    assert result.created_at != null
  }

  void testExpand() {
    def shortened = g.shorten(url)
    def result = g.expand(shortened.url)

    assert result.error == null
    assert result.short_url == shortened.url
    assert result.long_url == url
  }

  void testShortenFailure() {
    shouldFail() { ->
      def result = g.shorten("")
    }
  }
  
  void testInfoFailure() {
    shouldFail() { ->
      def result = g.info("")
    }
  }
  
  void testExpandFailure() {
    shouldFail() { ->
      def result = g.expand("")
    }
  }
}

