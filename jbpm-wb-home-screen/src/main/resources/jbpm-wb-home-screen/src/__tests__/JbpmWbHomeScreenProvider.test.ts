import { JbpmWbHomeScreenProvider } from "../JbpmWbHomeScreenProvider";
import { Profile } from "@kiegroup-ts-generated/kie-wb-common-profile-api";
import { AppFormer } from "appformer-js";
import * as HomeApi from "kie-wb-common-home-api";

const translationMap = new Map<string, string>([
  ["Heading", "Welcome to jBPM"],
  [
    "SubHeading",
    "jBoss Business Process Management (jBPM) Suite offers a set of flexible tools, that support the way you need " +
      "to work. Select a tool below to get started."
  ],
  ["Design", "Design"],
  ["DesignDescription", "Model, build, and publish your artifacts."],
  ["DevOps", "DevOps"],
  ["DevOpsDescription", "Run and manage servers and active instances."],
  ["Manage", "Manage"],
  ["ManageDescription", "Run, assign, and keep track of business processes."],
  ["Track", "Track"],
  ["TrackDescription", "Create reports for active business processes."]
]);

describe("JbpmWbHomeScreenProvider", () => {
  describe("get", () => {
    beforeEach(() => {
      AppFormer.prototype.translate = jest.fn((key: string): string => translationMap.get(key)!);
    });

    afterEach(() => {
      jest.resetAllMocks();
    });

    test("with FULL and PLANNER_AND_RULES profile", () => {
      [Profile.FULL, Profile.PLANNER_AND_RULES].forEach(profile => {
        const model = new JbpmWbHomeScreenProvider().get(profile);

        expect(model.welcomeText).toEqual("Welcome to jBPM");
        expect(model.description).toEqual(
          "jBoss Business Process Management (jBPM) Suite offers a set of flexible tools, that support the way you " +
            "need to work. Select a tool below to get started."
        );
        expect(model.backgroundImageUrl).toEqual("images/home_bg.jpg");

        const cards = model.cards;
        expect(cards).toHaveLength(4);

        const designCard = model.cards[0];
        expect(designCard.iconCssClasses).toStrictEqual(["pficon", "pficon-blueprint"]);
        expect(designCard.title).toEqual("Design");
        expect(designCard.perspectiveId).toEqual("LibraryPerspective");
        expect(designCard.onMayClick).toBeUndefined();
        expect(designCard.description.elements).toHaveLength(1);
        expect(designCard.description.elements[0].isText()).toBeTruthy();
        expect((designCard.description.elements[0] as HomeApi.CardDescriptionTextElement).text).toEqual(
          "Model, build, and publish your artifacts."
        );

        const devOpsCard = model.cards[1];
        expect(devOpsCard.iconCssClasses).toStrictEqual(["fa", "fa-gears"]);
        expect(devOpsCard.title).toEqual("DevOps");
        expect(devOpsCard.perspectiveId).toEqual("ServerManagementPerspective");
        expect(devOpsCard.onMayClick).toBeUndefined();
        expect(devOpsCard.description.elements).toHaveLength(1);
        expect(devOpsCard.description.elements[0].isText()).toBeTruthy();
        expect((devOpsCard.description.elements[0] as HomeApi.CardDescriptionTextElement).text).toEqual(
          "Run and manage servers and active instances."
        );

        const manageCard = model.cards[2];
        expect(manageCard.iconCssClasses).toStrictEqual(["fa", "fa-briefcase"]);
        expect(manageCard.title).toEqual("Manage");
        expect(manageCard.perspectiveId).toEqual("ProcessInstances");
        expect(manageCard.onMayClick).toBeUndefined();
        expect(manageCard.description.elements).toHaveLength(1);
        expect(manageCard.description.elements[0].isText()).toBeTruthy();
        expect((manageCard.description.elements[0] as HomeApi.CardDescriptionTextElement).text).toEqual(
          "Run, assign, and keep track of business processes."
        );

        const trackCard = model.cards[3];
        expect(trackCard.iconCssClasses).toStrictEqual(["pficon", "pficon-trend-up"]);
        expect(trackCard.title).toEqual("Track");
        expect(trackCard.perspectiveId).toEqual("ProcessDashboardPerspective");
        expect(trackCard.onMayClick).toBeUndefined();
        expect(trackCard.description.elements).toHaveLength(1);
        expect(trackCard.description.elements[0].isText()).toBeTruthy();
        expect((trackCard.description.elements[0] as HomeApi.CardDescriptionTextElement).text).toEqual(
          "Create reports for active business processes."
        );
      });
    });
  });
});
