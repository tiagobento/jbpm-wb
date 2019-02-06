import { MutationObserver } from './polyfills/mutation-observer';
import { configure } from "enzyme";
import Adapter from "enzyme-adapter-react-16";

configure({ adapter: new Adapter() });

Object.defineProperty(window, 'MutationObserver', { value: MutationObserver });
