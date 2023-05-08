package cn.itcast.hotel.constants;

public class HotelConstants {

    public static final String MAPPING_TEMPLATE_CREATE="{\n" +
            "  \"mappings\":{\n" +
            "    \"properties\":{\n" +
            "      \"id\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"address\":{\n" +
            "        \"type\":\"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"score\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\":\"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"city\":{\n" +
            "        \"type\":\"keyword\"\n" +
            "      },\n" +
            "      \"star_name\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"business\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"all\"\n" +
            "      },\n" +
            "      \"location\":{\n" +
            "        \"type\": \"geo_point\"\n" +
            "      },\n" +
            "      \"pic\":{\n" +
            "        \"type\":\"keyword\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "      \n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String MAPPING_TEMPLATE_CREATE_TEST="\n" +
            "{\n" +
            " \"name\":\"小王\",\n" +
            "\"age\":18\n" +
            "}";


    public static final String MAPPING_TEMPLATE_DELETE="DELETE /hotel";
}
