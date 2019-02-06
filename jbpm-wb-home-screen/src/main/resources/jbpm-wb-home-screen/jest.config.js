module.exports = {
    moduleDirectories: ["node_modules", "src"],
    moduleFileExtensions: ["js", "jsx", "ts", "tsx"],
    testRegex: "/__tests__/.*\\.test\\.(jsx?|tsx?)$",
    setupFiles: ["./test-env/jest-env-setup.js"],
    snapshotSerializers: ["<rootDir>/node_modules/enzyme-to-json/serializer"],
    transform: {
        "^.+\\.jsx?$": "babel-jest",
        "^.+\\.tsx?$": "ts-jest"
    },
    globals: {
      "ts-jest": {
        diagnostics: {
          pathRegex: ".*.test.tsx?$"
        }
      }
    }
};
