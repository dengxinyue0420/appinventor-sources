{
  // "This config file is used to compile the blockly code base."
  "id": "blockly-config",
  "paths": [],
  "inputs": [//'testalert.js'
    //blockly.js must come first
    '../lib/blockly/core/blockly.js',

    //msg/js/*.js has to come next (for constants)
    './src/msg/ko_kr/_messages.js',
    './src/msg/es_es/_messages.js',
    './src/msg/zh_tw/_messages.js',
    './src/msg/zh_cn/_messages.js',
    './src/msg/fr_fr/_messages.js',
    './src/msg/it_it/_messages.js',
    './src/msg/ru/_messages.js',
    './src/msg/sv/_messages.js',
    './src/msg/pt_br/_messages.js',
    './src/msg/en/_messages.js',

    // Lyn's instrumentation code
    "./src/instrument.js",

    //the rest of the blockly .js files can come in any order

    '../lib/blockly/core/block.js',
    '../lib/blockly/core/block_render_svg.js',
    '../lib/blockly/core/block_svg.js',
    '../lib/blockly/core/blocks.js',
    //'../lib/blockly/src/core/blockly.js',
    '../lib/blockly/core/bubble.js',
    '../lib/blockly/core/comment.js',
    '../lib/blockly/core/connection.js',
    '../lib/blockly/core/connection_db.js',
    '../lib/blockly/core/constants.js',
    '../lib/blockly/core/contextmenu.js',
    '../lib/blockly/core/css.js',
    '../lib/blockly/core/events.js',
    '../lib/blockly/core/field.js',
    '../lib/blockly/core/field_angle.js',
    '../lib/blockly/core/field_checkbox.js',
    '../lib/blockly/core/field_colour.js',
    '../lib/blockly/core/field_date.js',
    '../lib/blockly/core/field_dropdown.js',
    '../lib/blockly/core/field_image.js',
    '../lib/blockly/core/field_label.js',
    '../lib/blockly/core/field_number.js',
    '../lib/blockly/core/field_textinput.js',
    '../lib/blockly/core/field_variable.js',
    '../lib/blockly/core/flyout.js',
    '../lib/blockly/core/flyout_button.js',
    '../lib/blockly/core/generator.js',
    '../lib/blockly/core/icon.js',
    '../lib/blockly/core/inject.js',
    '../lib/blockly/core/input.js',
    '../lib/blockly/core/msg.js',
    '../lib/blockly/core/mutator.js',
    '../lib/blockly/core/names.js',
    '../lib/blockly/core/options.js',
    '../lib/blockly/core/procedures.js',
    //'../lib/blockly/src/core/realtime.js',  // removed from blockly
    //'../lib/blockly/src/core/realtime-client-utils.js',  // removed from blockly
    '../lib/blockly/core/rendered_connection.js',
    '../lib/blockly/core/scrollbar.js',
    //'../lib/blockly/src/core/toolbox.js',
    '../lib/blockly/core/tooltip.js',
    '../lib/blockly/core/trashcan.js',
    '../lib/blockly/core/utils.js',
    '../lib/blockly/core/variables.js',
    '../lib/blockly/core/warning.js',
    '../lib/blockly/core/widgetdiv.js',
    '../lib/blockly/core/workspace.js',
    '../lib/blockly/core/workspace_svg.js',
    '../lib/blockly/core/xml.js',
    '../lib/blockly/core/zoom_controls.js',

    //finally, include any of our own .js file in any order
    "./src/events.js",
    "./src/blocklyeditor.js",
    './src/typeblock.js',
    "./src/blockly.js",
    "./src/events.js",
    "./src/block.js",
    "./src/workspace.js",
    "./src/xml.js",
    "./src/trashcan.js",
    "./src/scrollbar.js",
    "./src/block_svg.js",
    "./src/component_database.js",
    "./src/procedure_database.js",
    "./src/variable_database.js",
    "./src/workspace_svg.js",
    "./src/blockColors.js",
    "./src/translation_properties.js",
    "./src/translation_events.js",
    "./src/translation_methods.js",
    "./src/translation_params.js",
    "./src/drawer.js",
    "./src/savefile.js",
    "./src/versioning.js",
    "./src/mutators.js",
    "./src/field_lexical_variable.js",
    "./src/errorIcon.js",
    "./src/warningHandler.js",
    "./src/field_procedure.js",
    "./src/field_textblockinput.js",
    "./src/warningIndicator.js",
    "./src/exportBlocksImage.js",
    "./src/flydown.js",
    "./src/field_flydown.js",
    "./src/field_parameter_flydown.js",
    "./src/field_global_flydown.js",
    "./src/field_procedure_flydown.js",
    "./src/nameSet.js",
    "./src/substitution.js",
    "./src/language_switch.js",
    "./src/warning.js",

    // Dialog Utiltiy
    "./src/util.js",

    // backpack files
    "./src/backpack.js",
    "./src/backpackFlyout.js",

    //blocks files
    './src/blocks/control.js',
    './src/blocks/logic.js',
    './src/blocks/text.js',
    './src/blocks/lists.js',
    './src/blocks/math.js',
    './src/blocks/utilities.js',
    './src/blocks/procedures.js',
    './src/blocks/lexical-variables.js',
    './src/blocks/colors.js',
    './src/blocks/components.js',

    //generator files
    "./src/generators/yail.js",
    "./src/generators/yail/componentblock.js",
    "./src/generators/yail/lists.js",
    "./src/generators/yail/math.js",
    "./src/generators/yail/control.js",
    "./src/generators/yail/logic.js",
    "./src/generators/yail/text.js",
    "./src/generators/yail/colors.js",
    "./src/generators/yail/variables.js",
    "./src/generators/yail/procedures.js",

    // Repl
    "./src/replmgr.js",

    // Group collaboration
    "./src/socket.io.js",
    "./src/collaboration.js"
    ],

  // This must be specified because datetimesymbols.js from the Closure Library
  // will be included, so when test-raw.html loads each input in RAW mode,
  // it is important that the proper charset be used.
  "output-charset": "UTF-8",
  "mode": "RAW",
//  "mode": "SIMPLE",
//  "mode" : "WHITESPACE_ONLY",
  "experimental-compiler-options": {
    "languageIn": "ECMASCRIPT5"
  },
  "closure-library" : "../lib/closure-library/closure/goog",
  "output-file": "../build/blocklyeditor/blockly-all.js"
}
