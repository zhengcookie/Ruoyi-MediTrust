import zh_cn from "./zh-cn";

export default function customTranslate(template: string, replacements: { [key: string]: string }) {
  replacements = replacements || {};
  // Translate
  for (const [key1, value] of Object.entries(zh_cn)) {
    if (key1 === template) {
      template = value || template;
      return template.replace(/{([^}]+)}/g, function (_: string, key: string) {
        return replacements[key] || "{" + key + "}";
      });
    }
  }
  return template;
}
