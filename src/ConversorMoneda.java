import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class ConversorMoneda {

    private static final String API_KEY = "443d209de28166235bb266f1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            mostrarMenu();
            opcion = scanner.nextInt();

            if (opcion >= 1 && opcion <= 6) {
                System.out.print("Ingrese el valor que deseas convertir: ");
                double valor = scanner.nextDouble();

                String fromCurrency = obtenerMonedaOrigen(opcion);
                String toCurrency = obtenerMonedaDestino(opcion);

                try {
                    double tasaConversion = obtenerTasaConversion(fromCurrency, toCurrency);
                    double resultado = valor * tasaConversion;

                    System.out.printf("El valor %.2f [%s] corresponde al valor final de >>> %.2f [%s]%n",
                            valor, fromCurrency, resultado, toCurrency);
                } catch (IOException e) {
                    System.out.println("Error al obtener la tasa de conversión: " + e.getMessage());
                }
            } else if (opcion == 7) {
                System.out.println("¡Gracias por usar el conversor! ¡Hasta pronto!");
            } else {
                System.out.println("Opción inválida. Por favor, intenta de nuevo.");
            }
        } while (opcion != 7);

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("*************************************************");
        System.out.println("   Sea bienvenido/a al Conversor de Moneda =]");
        System.out.println("*************************************************");
        System.out.println("1) Dólar >> Peso argentino");
        System.out.println("2) Peso argentino >> Dólar");
        System.out.println("3) Dólar >> Real brasileño");
        System.out.println("4) Real brasileño >> Dólar");
        System.out.println("5) Dólar >> Peso colombiano");
        System.out.println("6) Peso colombiano >> Dólar");
        System.out.println("7) Salir");
        System.out.println("*************************************************");
        System.out.print("Elija una opción válida: ");
    }

    private static String obtenerMonedaOrigen(int opcion) {
        return switch (opcion) {
            case 1, 3, 5 -> "USD";
            case 2 -> "ARS";
            case 4 -> "BRL";
            case 6 -> "COP";
            default -> "";
        };
    }

    private static String obtenerMonedaDestino(int opcion) {
        return switch (opcion) {
            case 1 -> "ARS";
            case 2 -> "USD";
            case 3 -> "BRL";
            case 4 -> "USD";
            case 5 -> "COP";
            case 6 -> "USD";
            default -> "";
        };
    }

    private static double obtenerTasaConversion(String fromCurrency, String toCurrency) throws IOException {
        String urlStr = BASE_URL + fromCurrency;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP error: " + conn.getResponseCode());
        }

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder jsonString = new StringBuilder();
        while (scanner.hasNext()) {
            jsonString.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject jsonObject = new JSONObject(jsonString.toString());
        return jsonObject.getJSONObject("conversion_rates").getDouble(toCurrency);
    }
}
