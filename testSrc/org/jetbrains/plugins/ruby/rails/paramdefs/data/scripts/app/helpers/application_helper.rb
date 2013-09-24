module ApplicationHelper
    def test()
        ActionView::Helpers::AssetTagHelper::javascript_include_tag('calendar/calendar')
#                                                                              <caret>
    end
end