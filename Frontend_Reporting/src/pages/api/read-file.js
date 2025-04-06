import fs from 'fs';
import path from 'path';

export default function handler(req, res) {
  const filePath=path.join(process.cwd(),'..','path.txt')
  const output_dir_path = fs.readFileSync(filePath, 'utf-8').replace(/[\r\n]+/gm, "");
  var consolidated_report = fs.readdirSync(output_dir_path).filter(fn => fn.match('Consolidated_.*_OptimizerReport.txt'));
  const file=path.join(output_dir_path,consolidated_report[0])
  console.log(file);
  try {
    const fileContents = fs.readFileSync(file, 'utf-8');
    res.status(200).send(fileContents);
  } catch (error) {
    res.status(500).send('Error reading file');
  }
}
