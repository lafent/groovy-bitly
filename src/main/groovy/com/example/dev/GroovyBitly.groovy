package com.example.dev

import com.example.dev.BitlyException
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT

class GroovyBitly {
  private apiEndPoint = "http://api.bit.ly"
  private userAgent = "Groovy ${groovy.lang.GroovySystem.getVersion()} + HTTPBuilder"
  private slurper = new JsonSlurper()
  private http = new HTTPBuilder()
  private apiUrls = [
    'expand': '/v3/expand',
    'info': '/v3/info',
    'shorten': '/v3/shorten',
    'keyword': '/v3/keyword',
    ]
  
  def username = System.getenv()['BITLY_USER']
  def apikey = System.getenv()['BITLY_APIKEY']
 
 GroovyBitly() { }

  GroovyBitly(uname, akey) {
    username = uname
    apikey = akey
  }

  private makeGetRequest(path, query) {
    http.request(apiEndPoint, GET, TEXT) { req ->
      uri.path = path
      uri.query = query
      headers.'User-Agent' = userAgent
      headers.Accept = 'application/json'

      response.success = { resp, reader ->
        assert resp.statusLine.statusCode == 200
        def output = slurper.parseText(reader.text)
        return output
      }
      response.'404' = {
        throw new BitlyException("API End Point Not Found")
      }

      response.'500' = { 
        throw new BitlyException("Remote End Server Error")
      }
    }
  }
 
  def shorten = { longUri ->
    if (longUri.size() < 10) {
      throw new BitlyException("Unable to Parse Empty String");
    }

    def results = makeGetRequest(apiUrls['shorten'], [login: username, apiKey: apikey, uri: longUri])
    return results.data
  }

  def info = { url -> 
    if (url.size() < 10) {
      throw new BitlyException("Unable to Parse Empty String");
    }

    def results = makeGetRequest(apiUrls['info'], [login: username, apiKey: apikey, shortUrl: url])
    return results.data.info[0]
  }

  def expand = { url -> 
    if (url.size() < 10) {
      throw new BitlyException("Unable to Parse Empty String");
    }
    
    def results = makeGetRequest(apiUrls['expand'], [login: username, apiKey: apikey, shortUrl: url])
    return results.data.expand[0]
  }
}

