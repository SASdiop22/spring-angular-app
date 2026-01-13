export const environment = {
  production: false,
  backend: {
    protocol: "http",
    host: "localhost",
    port: "8080"
  },
  apiBaseUrl: "http://localhost:8080/api"
};

export function geturl(): string {
  return environment.apiBaseUrl;
}
