package Utils;

import java.util.UUID;

public record HandShakeResult(int state, String name, UUID uuid, int version) {

}