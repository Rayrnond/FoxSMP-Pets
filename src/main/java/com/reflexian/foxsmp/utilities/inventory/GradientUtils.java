package com.reflexian.foxsmp.utilities.inventory;

public class GradientUtils {
//    final static Pattern pattern = Pattern.compile("<[^>]*>");
//
//    public static String stripColor(String string) {
//        string = org.bukkit.ChatColor.stripColor(string);
//        Matcher matcher = pattern.matcher(string);
//        string = matcher.replaceAll("");
//        return string;
//    }
////
////    public static String colorize(String string) {
////        try {
////            string = ChatColor.stripColor(string);
////            string = string.replaceAll("§", "");
////            var mm = MiniMessage.miniMessage();
////            Component parsed = mm.deserialize(string);
////            parsed = parsed.decoration(TextDecoration.ITALIC, false);
////            return "§r"+LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build().serialize(parsed);
////        }catch (Exception e) {
////            string = ChatColor.stripColor(string);
////            string = string.replaceAll("§", "");
////            return colorize(string);
////        }
////    }
//
//    public static Component getComponent(String string) {
//        string = ChatColor.stripColor(string);
//        string = string.replaceAll("§", "%%%");
//        var mm = MiniMessage.miniMessage();
//        return mm.deserialize(string).decoration(TextDecoration.ITALIC, false).replaceText(TextReplacementConfig.builder().match("%%%").replacement("§").build());
//    }
//
//    public static Component colorizeLore(String string) {
//        string = ChatColor.stripColor(string);
//        string = string.replaceAll("§", "");
//        var mm = MiniMessage.miniMessage();
//        return mm.deserialize(string).decoration(TextDecoration.ITALIC, false);
//    }
//
//    public static String[] extractHexStrings(String input) {
//        Matcher matcher = pattern.matcher(input);
//        String[] hexStrings = new String[2];
//        int index = 0;
//
//        while (matcher.find() && index < 2) {
//            String hexString = matcher.group(1);
//            hexString = "#" + hexString;
//            hexStrings[index] = hexString;
//            index++;
//        }
//
//        return hexStrings;
//    }
//
//    public static String createGradient(String text, String startColor, String endColor) {
//        // Convert hex colors to ChatColor
//        ChatColor startChatColor = ChatColor.of(startColor);
//        ChatColor endChatColor = ChatColor.of(endColor);
//
//        // Calculate the color gradient
//        int textLength = text.length();
//        double colorStep = 1.0 / (textLength - 1);
//        StringBuilder gradientBuilder = new StringBuilder();
//
//        for (int i = 0; i < textLength; i++) {
//            double ratio = colorStep * i;
//            ChatColor interpolatedColor = interpolateColor(startChatColor, endChatColor, ratio);
//            gradientBuilder.append(interpolatedColor).append(text.charAt(i));
//        }
//
//        return gradientBuilder.toString();
//    }
//
//    private static ChatColor interpolateColor(ChatColor startColor, ChatColor endColor, double ratio) {
//        // Interpolate the color components
//        int red = interpolateComponent(startColor.getColor().getRed(), endColor.getColor().getRed(), ratio);
//        int green = interpolateComponent(startColor.getColor().getGreen(), endColor.getColor().getGreen(), ratio);
//        int blue = interpolateComponent(startColor.getColor().getBlue(), endColor.getColor().getBlue(), ratio);
//
//        // Create the interpolated ChatColor
//        return ChatColor.of(new java.awt.Color(red, green, blue));
//    }
//
//    private static int interpolateComponent(int start, int end, double ratio) {
//        // Interpolate a single color component
//        int interpolatedComponent = (int) (start + (end - start) * ratio);
//        return Math.min(255, Math.max(0, interpolatedComponent));
//    }
}