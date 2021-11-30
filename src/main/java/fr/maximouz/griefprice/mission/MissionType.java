package fr.maximouz.griefprice.mission;

import fr.maximouz.griefprice.mission.missions.*;

import java.lang.reflect.InvocationTargetException;

public enum MissionType {

    LOG_BREAK(LogBreakMission.class, "Casser le plus de", "bûche possible."),
    REACH_LIMIT(ReachLimitMission.class, "Atteindre la couche 100 le", "plus de fois possible."),
    BREAK_GRASS(GrassBreakMission.class, "Casser le plus de bloc", "en terre."),
    PUSH_PLAYER(PushPlayerMission.class, "Pousser le plus de joueur à", "l'aide du bâton recul."),
    BREAK_STONE(StoneBreakMission.class, "Casser le plus de pierre", "(roche, pierre, andésite..)."),
    DOOR_PLACE(DoorPlaceMission.class, "Poser le plus de portes,", "barrières et portillons."),
    DYNAMITE_PLACE(DynamitePlaceMission.class, "Faire exploser le plus ", "de dynamite possible.");

    private final Class<? extends Mission> clazz;
    private final String[] description;

    MissionType(Class<? extends Mission> clazz, String... description) {
        this.clazz = clazz;
        this.description = description;
    }

    public String[] getDescription() {
        return description;
    }

    public Mission getNewMissionInstance() {
        try {
            return clazz.getDeclaredConstructor(getClass()).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MissionType getFromString(String name) {
        for (MissionType missionType : values())
            if (missionType.toString().equalsIgnoreCase(name))
                return missionType;

        return null;
    }


}
