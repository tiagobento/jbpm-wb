import * as AppFormer from "appformer-js";
import * as HomeApi from "kie-wb-common-home-api";
import { JbpmWbHomeScreenProvider } from "./JbpmWbHomeScreenProvider";

AppFormer.register(new HomeApi.HomeScreenAppFormerComponent(new JbpmWbHomeScreenProvider()));
