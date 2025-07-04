import { append as svgAppend, attr as svgAttr, create as svgCreate } from "tiny-svg";

const markerColor = {
  grey: "grey",
  red: "red",
  green: "green",
  blue: "#1890ff",
};
/**
 * 生成各种颜色的箭头
 *
 * @param defs
 * @param types
 */
const createMarker = (defs: SVGDefsElement, types: ("grey" | "red" | "green" | "blue")[] = ["grey", "red", "green", "blue"]) => {
  // const defs = domQuery("defs");
  if (!defs) {
    return;
  }

  types.forEach((type) => {
    const color = markerColor[type];

    // 创建sequenceflow-arrow-end
    const markerId1 = `sequenceflow-arrow-end-${type}`;
    if (!document.getElementById(markerId1)) {
      // 创建marker-end
      const marker1 = svgCreate("marker");
      svgAttr(marker1, {
        id: markerId1,
        viewBox: "0 0 20 20",
        refX: "11",
        refY: "10",
        markerWidth: "10",
        markerHeight: "10",
        orient: "auto",
      });
      // 创建path-end
      const path1 = svgCreate("path");
      svgAttr(path1, {
        d: "M 1 5 L 11 10 L 1 15 Z",
        style: `stroke-linecap: round; stroke-linejoin: round; stroke: ${color}; stroke-width: 1px; fill: ${color};`,
      });
      svgAppend(marker1, path1);
      svgAppend(defs, marker1);
    }

    // 创建conditional-flow-start
    const markerId2 = `conditional-flow-start-${type}`;
    if (!document.getElementById(markerId2)) {
      // 创建marker-start
      const marker2 = svgCreate("marker");
      svgAttr(marker2, {
        id: markerId2,
        viewBox: "0 0 20 20",
        refX: "-1",
        refY: "10",
        markerWidth: "10",
        markerHeight: "10",
        orient: "auto",
      });
      // 创建marker-start
      const path2 = svgCreate("path");
      svgAttr(path2, {
        d: "M 0 10 L 8 6 L 16 10 L 8 14 Z",
        style: `stroke-linecap: round; stroke-linejoin: round; stroke: ${color}; stroke-width: 2px; fill: white;`,
      });
      svgAppend(marker2, path2);
      svgAppend(defs, marker2);
    }
  });
};

export default createMarker;
