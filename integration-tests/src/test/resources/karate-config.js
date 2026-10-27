function fn() {
  var config = { baseUrl: karate.properties['api.url'] || 'http://localhost:8080' };
  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 15000);
  return config;
}
